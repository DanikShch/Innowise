package com.innowise.paymentservice.service;

import com.innowise.paymentservice.dto.PaymentRequestDto;
import com.innowise.paymentservice.dto.PaymentResponseDto;
import com.innowise.paymentservice.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface PaymentService {
    PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto);
    List<PaymentResponseDto> getPaymentsByOrderId(Long orderId);
    List<PaymentResponseDto> getPaymentsByUserId(Long userId);
    List<PaymentResponseDto> getPaymentsByStatus(PaymentStatus status);
    BigDecimal getTotalSum(Instant from, Instant to);
}
