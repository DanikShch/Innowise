package com.innowise.paymentservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderEvent {

    private Long orderId;
    private Long userId;
    private BigDecimal totalPrice;
    private Instant createdAt;
}
