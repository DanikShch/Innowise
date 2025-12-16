package com.innowise.paymentservice.dto;

import com.innowise.paymentservice.model.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class PaymentResponseDto {
    private String id;

    private Long orderId;

    private Long userId;

    private PaymentStatus status;

    private Instant timestamp;

    private BigDecimal paymentAmount;
}
