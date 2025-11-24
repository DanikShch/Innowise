package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.client.UserServiceClient;
import com.innowise.orderservice.dto.request.OrderItemRequestDto;
import com.innowise.orderservice.dto.request.OrderRequestDto;
import com.innowise.orderservice.dto.response.OrderResponseDto;
import com.innowise.orderservice.dto.response.UserResponseDto;
import com.innowise.orderservice.mapper.OrderItemMapper;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.model.Item;
import com.innowise.orderservice.model.Order;
import com.innowise.orderservice.model.OrderItem;
import com.innowise.orderservice.model.OrderStatus;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.service.OrderService;
import com.innowise.orderservice.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserServiceClient userServiceClient;
    private final SecurityUtil securityUtil;
    private final ItemRepository itemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        String userEmail = securityUtil.getCurrentUserEmail();
        UserResponseDto user = userServiceClient.getUserByEmail(userEmail);

        Order order = orderMapper.toEntity(orderRequestDto);
        order.setUserId(user.getId());
        order.setStatus(OrderStatus.PENDING);

        addOrderItemsToOrder(order, orderRequestDto.getItems());

        Order savedOrder = orderRepository.save(order);
        OrderResponseDto response = orderMapper.toDto(savedOrder);
        response.setUser(user);
        response.setTotalPrice(calculateOrderTotal(savedOrder));

        return response;
    }

    private BigDecimal calculateOrderTotal(Order order) {
        return order.getOrderItems().stream()
                .map(orderItem -> orderItem.getItem().getPrice()
                        .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public OrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return convertToOrderResponseDto(order);
    }

    @Override
    public List<OrderResponseDto> getOrdersByIds(List<Long> ids) {
        List<Order> orders = orderRepository.findByIdIn(ids);
        return orders.stream()
                .map(this::convertToOrderResponseDto)
                .toList();
    }

    @Override
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
        return response;
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrder(Long id, OrderRequestDto orderRequestDto) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        String currentUserEmail = securityUtil.getCurrentUserEmail();
        UserResponseDto currentUser = userServiceClient.getUserByEmail(currentUserEmail);

        if (!existingOrder.getUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only update your own orders");
        }

        if (existingOrder.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Only orders with PENDING status can be updated");
        }

        existingOrder.getOrderItems().clear();
        addOrderItemsToOrder(existingOrder, orderRequestDto.getItems());

        Order updatedOrder = orderRepository.save(existingOrder);
        return convertToOrderResponseDto(updatedOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        String currentUserEmail = securityUtil.getCurrentUserEmail();
        UserResponseDto currentUser = userServiceClient.getUserByEmail(currentUserEmail);

        if (!order.getUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own orders");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Only orders with PENDING status can be deleted");
        }
        orderRepository.delete(order);
    }

    private void addOrderItemsToOrder(Order order, List<OrderItemRequestDto> itemDtos) {
        itemDtos.forEach(itemDto -> {
            Item item = itemRepository.findById(itemDto.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemDto.getItemId()));

            OrderItem orderItem = orderItemMapper.toEntity(itemDto);
            orderItem.setOrder(order);
            orderItem.setItem(item);
            order.getOrderItems().add(orderItem);
        });
    }
}