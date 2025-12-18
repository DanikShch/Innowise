package com.innowise.orderservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderEvent {
    private Long orderId;
    private Long userId;
    private BigDecimal totalPrice;
    private Instant createdAt;
}
