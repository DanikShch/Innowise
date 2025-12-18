package com.innowise.paymentservice.service;

import com.innowise.paymentservice.dto.PaymentResponseDto;
import com.innowise.paymentservice.kafka.event.CreateOrderEvent;
import com.innowise.paymentservice.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface PaymentService {
    List<PaymentResponseDto> getPaymentsByOrderId(Long orderId);
    List<PaymentResponseDto> getPaymentsByUserId(Long userId);
    List<PaymentResponseDto> getPaymentsByStatus(PaymentStatus status);
    BigDecimal getTotalSum(Instant from, Instant to);
    void processOrderPayment(CreateOrderEvent event);
    List<PaymentResponseDto> getMyPayments();
}
