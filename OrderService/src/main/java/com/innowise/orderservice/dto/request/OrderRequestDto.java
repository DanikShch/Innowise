package com.innowise.orderservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {
    @NotNull(message = "Items cannot be null")
    @Size(min = 1, message = "Order must contain at least one item")
    private List<OrderItemRequestDto> items;
}
