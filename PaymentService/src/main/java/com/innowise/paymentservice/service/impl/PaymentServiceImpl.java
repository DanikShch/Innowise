package com.innowise.paymentservice.service.impl;

import com.innowise.paymentservice.Repository.PaymentRepository;
import com.innowise.paymentservice.client.RandomNumberClient;
import com.innowise.paymentservice.dto.PaymentRequestDto;
import com.innowise.paymentservice.dto.PaymentResponseDto;
import com.innowise.paymentservice.kafka.event.CreateOrderEvent;
import com.innowise.paymentservice.mapper.PaymentMapper;
import com.innowise.paymentservice.model.Payment;
import com.innowise.paymentservice.model.PaymentStatus;
import com.innowise.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final RandomNumberClient randomNumberClient;

    @Override
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto){
        Payment payment = paymentMapper.toEntity(paymentRequestDto);
        int randomNumber = randomNumberClient.getRandomNumber();
        if (randomNumber % 2 == 0) {
            payment.setStatus(PaymentStatus.SUCCESS);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }
        payment.setTimestamp(Instant.now());
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    public List<PaymentResponseDto> getPaymentsByOrderId(Long orderId) {
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        return payments.stream().map(paymentMapper::toDto).toList();
    }

    @Override
    public List<PaymentResponseDto> getPaymentsByUserId(Long userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        return payments.stream().map(paymentMapper::toDto).toList();
    }

    @Override
    public List<PaymentResponseDto> getPaymentsByStatus(PaymentStatus status) {
        List<Payment> payments = paymentRepository.findByStatus(status);
        return payments.stream().map(paymentMapper::toDto).toList();
    }

    @Override
    public BigDecimal getTotalSum(Instant from, Instant to) {
        List<Payment> payments = paymentRepository.findByTimestampBetween(from, to);
        return payments.stream().filter(payment -> payment.getStatus().equals(PaymentStatus.SUCCESS))
                .map(Payment::getPaymentAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void processOrderPayment(CreateOrderEvent event) {

        Payment payment = Payment.builder()
                .orderId(event.getOrderId())
                .userId(event.getUserId())
                .paymentAmount(event.getTotalPrice())
                .timestamp(Instant.now())
                .build();

        int randomNumber = randomNumberClient.getRandomNumber();

        if (randomNumber % 2 == 0) {
            payment.setStatus(PaymentStatus.SUCCESS);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        paymentRepository.save(payment);
    }

}
