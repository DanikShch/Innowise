package com.innowise.paymentservice.kafka.produser;

import com.innowise.paymentservice.kafka.event.CreatePaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    @Value("${app.kafka.create-payment-topic}")
    private String createPaymentTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(CreatePaymentEvent event) {
        kafkaTemplate.send(createPaymentTopic, event.getOrderId().toString(), event);
    }
}
