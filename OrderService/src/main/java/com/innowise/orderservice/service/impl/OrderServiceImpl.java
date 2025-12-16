package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.client.UserServiceClient;
import com.innowise.orderservice.dto.request.OrderItemRequestDto;
import com.innowise.orderservice.dto.request.OrderRequestDto;
import com.innowise.orderservice.dto.response.OrderResponseDto;
import com.innowise.orderservice.dto.response.UserResponseDto;
import com.innowise.orderservice.exception.AccessDeniedException;
import com.innowise.orderservice.exception.InvalidOrderStatusException;
import com.innowise.orderservice.exception.ItemNotFoundException;
import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.kafka.event.CreateOrderEvent;
import com.innowise.orderservice.kafka.producer.OrderEventProducer;
import com.innowise.orderservice.mapper.OrderItemMapper;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.model.Item;
import com.innowise.orderservice.model.Order;
import com.innowise.orderservice.model.OrderItem;
import com.innowise.orderservice.model.OrderStatus;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserServiceClient userServiceClient;
    private final ItemRepository itemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderEventProducer orderEventProducer;

    @Override
    @Transactional
    @CacheEvict(value = "orders", allEntries = true)
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        UserResponseDto user = userServiceClient.getCurrentUser();

        Order order = orderMapper.toEntity(orderRequestDto);
        order.setUserId(user.getId());
        order.setStatus(OrderStatus.PENDING);

        addOrderItemsToOrder(order, orderRequestDto.getItems());

        Order savedOrder = orderRepository.save(order);

        BigDecimal totalPrice = calculateOrderTotal(savedOrder);

        CreateOrderEvent event = CreateOrderEvent.builder()
                .orderId(savedOrder.getId())
                .userId(savedOrder.getUserId())
                .totalPrice(totalPrice)
                .createdAt(Instant.now())
                .build();

        orderEventProducer.sendCreateOrderEvent(event);

        OrderResponseDto response = orderMapper.toDto(savedOrder);
        response.setUser(user);
        response.setTotalPrice(totalPrice);

        return response;
    }

    private BigDecimal calculateOrderTotal(Order order) {
        return order.getOrderItems().stream()
                .map(orderItem -> orderItem.getItem().getPrice()
                        .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Cacheable(value = "orders", key = "#id")
    public OrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        checkOrderAccess(order);
        return convertToOrderResponseDto(order);
    }

    @Override
    @Cacheable(value = "orders", key = "'ids_' + #ids.hashCode()")
    public List<OrderResponseDto> getOrdersByIds(List<Long> ids) {
        List<Order> orders = orderRepository.findByIdIn(ids);
        return orders.stream()
                .map(this::convertToOrderResponseDto)
                .toList();
    }

    @Override
    @Cacheable(value = "orders", key = "'statuses_' + #statuses.hashCode()")
    public List<OrderResponseDto> getOrdersByStatuses(List<OrderStatus> statuses) {
        List<Order> orders = orderRepository.findByStatusIn(statuses);
        return orders.stream()
                .map(this::convertToOrderResponseDto)
                .toList();
    }

    private OrderResponseDto convertToOrderResponseDto(Order order) {
        UserResponseDto user = userServiceClient.getUserById(order.getUserId());
        OrderResponseDto response = orderMapper.toDto(order);
        response.setUser(user);
        response.setTotalPrice(calculateOrderTotal(order));

        if (response.getItems() != null) {
            response.getItems().forEach(item ->
                    item.setItemTotal(item.getItem().getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity())))
            );
        }
        return response;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "orders", key = "#id"),
            @CacheEvict(value = "orders", allEntries = true)
    })
    public OrderResponseDto updateOrder(Long id, OrderRequestDto orderRequestDto) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        checkOrderAccess(existingOrder);

        if (existingOrder.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException("Only orders with PENDING status can be updated");
        }

        existingOrder.getOrderItems().clear();
        addOrderItemsToOrder(existingOrder, orderRequestDto.getItems());

        Order updatedOrder = orderRepository.save(existingOrder);
        return convertToOrderResponseDto(updatedOrder);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "orders", key = "#id"),
            @CacheEvict(value = "orders", allEntries = true)
    })
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        checkOrderAccess(order);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException("Only orders with PENDING status can be deleted");
        }
        orderRepository.delete(order);
    }

    private void addOrderItemsToOrder(Order order, List<OrderItemRequestDto> itemDtos) {
        itemDtos.forEach(itemDto -> {
            Item item = itemRepository.findById(itemDto.getItemId())
                    .orElseThrow(() -> new ItemNotFoundException(itemDto.getItemId()));

            OrderItem orderItem = orderItemMapper.toEntity(itemDto);
            orderItem.setOrder(order);
            orderItem.setItem(item);
            order.getOrderItems().add(orderItem);
        });
    }

    private void checkOrderAccess(Order order) {
        UserResponseDto currentUser = userServiceClient.getCurrentUser();

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwnOrder = order.getUserId().equals(currentUser.getId());

        if (!isAdmin && !isOwnOrder) {
            throw new AccessDeniedException("You can only access your own orders");
        }
    }
}