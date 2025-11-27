package com.innowise.orderservice.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponseDto {
    private ItemResponseDto item;
    private Long quantity;
    private BigDecimal itemTotal;
}
