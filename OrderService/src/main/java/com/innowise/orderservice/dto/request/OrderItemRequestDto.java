package com.innowise.orderservice.dto.request;

import jakarta.validation.constraints.NotNull;

public class OrderItemRequestDto {
    @NotNull(message = "Item ID cannot be null")
    private Long itemId;

    @NotNull(message = "Quantity cannot be null")
    private Long quantity;
}
