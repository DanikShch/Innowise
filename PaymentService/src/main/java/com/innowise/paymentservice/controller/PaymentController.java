package com.innowise.paymentservice.controller;

import com.innowise.paymentservice.dto.PaymentResponseDto;
import com.innowise.paymentservice.model.PaymentStatus;
import com.innowise.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping(params = "orderId")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByOrderId(@RequestParam Long orderId) {
        List<PaymentResponseDto> payments = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping(params = "userId")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByUserId(@RequestParam Long userId) {
        List<PaymentResponseDto> payments = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping(params = "status")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByStatus(@RequestParam PaymentStatus status) {
        List<PaymentResponseDto> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalSum(@RequestParam Instant from, @RequestParam Instant to) {
        BigDecimal totalSum = paymentService.getTotalSum(from, to);
        return ResponseEntity.ok(totalSum);
    }

    @GetMapping("/my")
    public ResponseEntity<List<PaymentResponseDto>> getMyPayments() {
        return ResponseEntity.ok(paymentService.getMyPayments());
    }
}
