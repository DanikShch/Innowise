package com.innowise.orderservice.unit;

import com.innowise.orderservice.client.UserServiceClient;
import com.innowise.orderservice.dto.request.OrderItemRequestDto;
import com.innowise.orderservice.dto.request.OrderRequestDto;
import com.innowise.orderservice.dto.response.OrderResponseDto;
import com.innowise.orderservice.dto.response.UserResponseDto;
import com.innowise.orderservice.exception.*;
import com.innowise.orderservice.kafka.event.CreateOrderEvent;
import com.innowise.orderservice.kafka.producer.OrderEventProducer;
import com.innowise.orderservice.mapper.OrderItemMapper;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.model.*;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.service.impl.OrderServiceImpl;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class OrderServiceImplTest {

    @Mock
    OrderEventProducer orderEventProducer;
    @Mock
    OrderRepository orderRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    OrderMapper orderMapper;
    @Mock
    OrderItemMapper orderItemMapper;
    @Mock
    UserServiceClient userServiceClient;

    @InjectMocks
    OrderServiceImpl orderService;

    private UserResponseDto user(Long id) {
        return UserResponseDto.builder()
                .id(id)
                .name("John")
                .surname("Doe")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
    }

    private Order order(Long id, OrderStatus status) {
        Order o = new Order();
        o.setId(id);
        o.setUserId(1L);
        o.setStatus(status);
        o.setCreationDate(LocalDateTime.now());
        return o;
    }

    @Test
    void createOrder_Success() {
        UserResponseDto user = user(1L);

        OrderRequestDto req = new OrderRequestDto();
        OrderItemRequestDto itemReq = new OrderItemRequestDto(1L, 2L);
        req.setItems(List.of(itemReq));

        Item item = new Item();
        item.setId(1L);
        item.setPrice(new BigDecimal("99.99"));

        Order mapped = order(null, OrderStatus.PENDING);
        Order saved = order(1L, OrderStatus.PENDING);

        when(userServiceClient.getCurrentUser()).thenReturn(user);
        when(orderMapper.toEntity(req)).thenReturn(mapped);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(orderItemMapper.toEntity(itemReq)).thenReturn(new OrderItem());
        when(orderRepository.save(mapped)).thenReturn(saved);
        when(orderMapper.toDto(saved)).thenReturn(new OrderResponseDto());

        doNothing().when(orderEventProducer)
                .sendCreateOrderEvent(any(CreateOrderEvent.class));
        OrderResponseDto result = orderService.createOrder(req);

        assertNotNull(result);
        verify(orderRepository).save(mapped);
        verify(orderEventProducer).sendCreateOrderEvent(any(CreateOrderEvent.class));
    }

    @Test
    void createOrder_ItemNotFound() {
        UserResponseDto user = user(1L);

        OrderRequestDto req = new OrderRequestDto();
        req.setItems(List.of(new OrderItemRequestDto(999L, 1L)));

        when(userServiceClient.getCurrentUser()).thenReturn(user);
        when(orderMapper.toEntity(req)).thenReturn(new Order());
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> orderService.createOrder(req));
    }

    @Test
    void getOrderById_Success_WhenOwner() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            SecurityContext sc = mock(SecurityContext.class);
            Authentication auth = mock(Authentication.class);
            mocked.when(SecurityContextHolder::getContext).thenReturn(sc);
            when(sc.getAuthentication()).thenReturn(auth);
            doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                    .when(auth)
                    .getAuthorities();


            UserResponseDto user = user(1L);
            Order order = order(1L, OrderStatus.PENDING);

            OrderResponseDto dto = new OrderResponseDto();
            dto.setId(1L);
            dto.setUser(user);

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(userServiceClient.getCurrentUser()).thenReturn(user);
            when(userServiceClient.getUserById(1L)).thenReturn(user);
            when(orderMapper.toDto(order)).thenReturn(dto);

            OrderResponseDto result = orderService.getOrderById(1L);

            assertEquals(1L, result.getId());
        }
    }

    @Test
    void getOrderById_AccessDenied() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {

            SecurityContext sc = mock(SecurityContext.class);
            Authentication auth = mock(Authentication.class);
            mocked.when(SecurityContextHolder::getContext).thenReturn(sc);
            when(sc.getAuthentication()).thenReturn(auth);

            doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                    .when(auth)
                    .getAuthorities();


            UserResponseDto user = user(2L);
            Order order = order(1L, OrderStatus.PENDING);

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(userServiceClient.getCurrentUser()).thenReturn(user);

            assertThrows(AccessDeniedException.class, () -> orderService.getOrderById(1L));
        }
    }

    @Test
    void getOrderById_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(1L));
    }

    @Test
    void getOrdersByIds_Success() {
        Order o1 = order(1L, OrderStatus.PENDING);
        Order o2 = order(2L, OrderStatus.PENDING);
        UserResponseDto user = user(1L);

        when(orderRepository.findByIdIn(List.of(1L, 2L))).thenReturn(List.of(o1, o2));
        when(userServiceClient.getUserById(1L)).thenReturn(user);
        when(orderMapper.toDto(o1)).thenReturn(new OrderResponseDto());
        when(orderMapper.toDto(o2)).thenReturn(new OrderResponseDto());

        List<OrderResponseDto> result = orderService.getOrdersByIds(List.of(1L, 2L));

        assertEquals(2, result.size());
    }

    @Test
    void updateOrder_Success() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {

            SecurityContext sc = mock(SecurityContext.class);
            Authentication auth = mock(Authentication.class);
            mocked.when(SecurityContextHolder::getContext).thenReturn(sc);
            when(sc.getAuthentication()).thenReturn(auth);
            doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                    .when(auth)
                    .getAuthorities();


            UserResponseDto user = user(1L);
            Order existing = order(1L, OrderStatus.PENDING);

            OrderRequestDto req = new OrderRequestDto();
            req.setItems(Collections.emptyList());

            when(orderRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(userServiceClient.getCurrentUser()).thenReturn(user);
            when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));
            when(userServiceClient.getUserById(1L)).thenReturn(user);
            when(orderMapper.toDto(existing)).thenReturn(new OrderResponseDto());

            OrderResponseDto result = orderService.updateOrder(1L, req);

            assertNotNull(result);
        }
    }

    @Test
    void updateOrder_InvalidStatus() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {

            SecurityContext sc = mock(SecurityContext.class);
            Authentication auth = mock(Authentication.class);
            mocked.when(SecurityContextHolder::getContext).thenReturn(sc);
            when(sc.getAuthentication()).thenReturn(auth);

            doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                    .when(auth)
                    .getAuthorities();


            UserResponseDto user = user(1L);
            Order order = order(1L, OrderStatus.DELIVERED);

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(userServiceClient.getCurrentUser()).thenReturn(user);

            assertThrows(InvalidOrderStatusException.class,
                    () -> orderService.updateOrder(1L, new OrderRequestDto()));
        }
    }

    @Test
    void updateOrder_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class,
                () -> orderService.updateOrder(1L, new OrderRequestDto()));
    }

    @Test
    void deleteOrder_Success() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {

            SecurityContext sc = mock(SecurityContext.class);
            Authentication auth = mock(Authentication.class);
            mocked.when(SecurityContextHolder::getContext).thenReturn(sc);
            when(sc.getAuthentication()).thenReturn(auth);

            doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                    .when(auth)
                    .getAuthorities();


            UserResponseDto user = user(1L);
            Order order = order(1L, OrderStatus.PENDING);

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(userServiceClient.getCurrentUser()).thenReturn(user);

            orderService.deleteOrder(1L);

            verify(orderRepository).delete(order);
        }
    }

    @Test
    void deleteOrder_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(1L));
    }
}
