package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.request.OrderRequestDto;
import com.innowise.orderservice.dto.response.OrderResponseDto;
import com.innowise.orderservice.model.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponseDto createOrder(OrderRequestDto orderRequestDto);
    OrderResponseDto getOrderById(Long id);
    List<OrderResponseDto> getOrdersByIds(List<Long> ids);
    List<OrderResponseDto> getOrdersByStatuses(List<OrderStatus> statuses);
    OrderResponseDto updateOrder(Long id, OrderRequestDto orderRequestDto);
    void deleteOrder(Long id);
}