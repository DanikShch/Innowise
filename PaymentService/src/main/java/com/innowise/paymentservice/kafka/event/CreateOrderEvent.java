package com.innowise.paymentservice.kafka.event;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class CreateOrderEvent {

    private Long orderId;
    private Long userId;
    private BigDecimal totalPrice;
    private Instant createdAt;
}
