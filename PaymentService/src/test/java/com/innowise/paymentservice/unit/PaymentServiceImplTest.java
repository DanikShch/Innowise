package com.innowise.paymentservice.unit;

import com.innowise.paymentservice.repository.PaymentRepository;
import com.innowise.paymentservice.client.RandomNumberClient;
import com.innowise.paymentservice.dto.PaymentResponseDto;
import com.innowise.paymentservice.kafka.event.CreateOrderEvent;
import com.innowise.paymentservice.kafka.event.CreatePaymentEvent;
import com.innowise.paymentservice.kafka.produser.PaymentEventProducer;
import com.innowise.paymentservice.mapper.PaymentMapper;
import com.innowise.paymentservice.model.Payment;
import com.innowise.paymentservice.model.PaymentStatus;
import com.innowise.paymentservice.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private RandomNumberClient randomNumberClient;

    @Mock
    private PaymentEventProducer paymentEventProducer;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;
    private PaymentResponseDto responseDto;

    private void setupTestData() {
        payment = Payment.builder()
                .orderId(1L)
                .userId(10L)
                .paymentAmount(new BigDecimal("100.00"))
                .status(PaymentStatus.SUCCESS)
                .timestamp(Instant.now())
                .build();

        responseDto = new PaymentResponseDto();
        responseDto.setOrderId(1L);
        responseDto.setUserId(10L);
        responseDto.setPaymentAmount(new BigDecimal("100.00"));
        responseDto.setStatus(PaymentStatus.SUCCESS);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getPaymentsByOrderId_Success() {
        setupTestData();

        when(paymentRepository.findByOrderId(1L)).thenReturn(List.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(responseDto);

        List<PaymentResponseDto> result = paymentService.getPaymentsByOrderId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getOrderId());

        verify(paymentRepository).findByOrderId(1L);
        verify(paymentMapper).toDto(payment);
    }


    @Test
    void getPaymentsByUserId_Success() {
        setupTestData();

        when(paymentRepository.findByUserId(10L)).thenReturn(List.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(responseDto);

        List<PaymentResponseDto> result = paymentService.getPaymentsByUserId(10L);

        assertEquals(1, result.size());
        verify(paymentRepository).findByUserId(10L);
    }

    @Test
    void getPaymentsByStatus_Success() {
        setupTestData();

        when(paymentRepository.findByStatus(PaymentStatus.SUCCESS))
                .thenReturn(List.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(responseDto);

        List<PaymentResponseDto> result =
                paymentService.getPaymentsByStatus(PaymentStatus.SUCCESS);

        assertEquals(1, result.size());
        verify(paymentRepository).findByStatus(PaymentStatus.SUCCESS);
    }


    @Test
    void getTotalSum_OnlySuccessPayments() {
        Payment successPayment = Payment.builder()
                .paymentAmount(new BigDecimal("100.00"))
                .status(PaymentStatus.SUCCESS)
                .build();

        Payment failedPayment = Payment.builder()
                .paymentAmount(new BigDecimal("50.00"))
                .status(PaymentStatus.FAILED)
                .build();

        when(paymentRepository.findByTimestampBetween(any(), any()))
                .thenReturn(List.of(successPayment, failedPayment));

        BigDecimal sum = paymentService.getTotalSum(
                Instant.now().minusSeconds(3600),
                Instant.now()
        );

        assertEquals(new BigDecimal("100.00"), sum);
    }



    @Test
    void processOrderPayment_SuccessPayment() {
        CreateOrderEvent event = new CreateOrderEvent(
                1L,
                10L,
                new BigDecimal("200.00"),
                Instant.now()
        );


        when(randomNumberClient.getRandomNumber()).thenReturn(2);
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        paymentService.processOrderPayment(event);

        verify(paymentRepository).save(any(Payment.class));
        verify(paymentEventProducer).send(any(CreatePaymentEvent.class));
    }

    @Test
    void processOrderPayment_FailedPayment() {
        CreateOrderEvent event = new CreateOrderEvent(
                1L,
                10L,
                new BigDecimal("200.00"),
                Instant.now()
        );


        when(randomNumberClient.getRandomNumber()).thenReturn(3);
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        paymentService.processOrderPayment(event);

        verify(paymentRepository).save(any(Payment.class));
        verify(paymentEventProducer).send(any(CreatePaymentEvent.class));
    }

    @Test
    void getMyPayments_Success() {
        setupTestData();

        var auth = new UsernamePasswordAuthenticationToken(
                null,
                10L
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(paymentRepository.findByUserId(10L)).thenReturn(List.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(responseDto);

        List<PaymentResponseDto> result = paymentService.getMyPayments();

        assertEquals(1, result.size());
        verify(paymentRepository).findByUserId(10L);
    }

    @Test
    void getMyPayments_Unauthenticated() {
        SecurityContextHolder.clearContext();

        assertThrows(RuntimeException.class,
                () -> paymentService.getMyPayments());
    }
}
