package com.innowise.orderservice.kafka.producer;

import com.innowise.orderservice.kafka.event.CreateOrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    @Value("${app.kafka.create-order-topic}")
    private String createOrderTopic;

    private final KafkaTemplate<String, CreateOrderEvent> kafkaTemplate;

    public void sendCreateOrderEvent(CreateOrderEvent event) {
        kafkaTemplate.send(createOrderTopic, event.getOrderId().toString(), event);
    }
}
