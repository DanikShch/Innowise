package com.innowise.paymentservice.kafka.consumer;

import com.innowise.paymentservice.kafka.event.CreateOrderEvent;
import com.innowise.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "${app.kafka.create-order-topic}", groupId = "${app.kafka.payment-group-id}")
    public void handleCreateOrder(CreateOrderEvent event) {
        paymentService.processOrderPayment(event);
    }
}
