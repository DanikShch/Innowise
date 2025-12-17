package com.innowise.paymentservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentEvent {

    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String status;
    private Instant timestamp;
}
