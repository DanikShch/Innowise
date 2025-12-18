package com.innowise.paymentservice.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.innowise.paymentservice.kafka.event.CreateOrderEvent;
import com.innowise.paymentservice.model.PaymentStatus;
import com.innowise.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.Instant;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WireMockTest(httpPort = 8089)
class PaymentServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void consumeCreateOrderEvent_failedPayment() {
        stubFor(get(urlEqualTo("/random"))
                .willReturn(okJson("[3]")));

        CreateOrderEvent event = new CreateOrderEvent(
                2L,
                20L,
                new BigDecimal("150.00"),
                Instant.now()
        );

        kafkaTemplate.send("create-order", event);

        await()
                .atMost(10, SECONDS)
                .untilAsserted(() -> {
                    var payments = paymentRepository.findByOrderId(2L);
                    assertEquals(1, payments.size());
                    assertEquals(PaymentStatus.FAILED, payments.getFirst().getStatus());
                });
    }

    @Test
    void consumeCreateOrderEvent_successPayment() {
        stubFor(get(urlEqualTo("/random"))
                .willReturn(okJson("[2]")));

        CreateOrderEvent event = new CreateOrderEvent(
                3L,
                30L,
                new BigDecimal("300.00"),
                Instant.now()
        );

        kafkaTemplate.send("create-order", event);

        await()
                .atMost(10, SECONDS)
                .untilAsserted(() -> {
                    var payments = paymentRepository.findByOrderId(3L);
                    assertEquals(1, payments.size());
                    assertEquals(PaymentStatus.SUCCESS, payments.getFirst().getStatus());
                });
    }
}
