package com.innowise.orderservice.kafka.event;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class CreatePaymentEvent {

    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String status;
    private Instant timestamp;
}
