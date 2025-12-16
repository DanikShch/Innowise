package com.innowise.orderservice.kafka.producer;

import com.innowise.orderservice.kafka.event.CreateOrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private static final String CREATE_ORDER_TOPIC = "create-order";

    private final KafkaTemplate<String, CreateOrderEvent> kafkaTemplate;

    public void sendCreateOrderEvent(CreateOrderEvent event) {
        kafkaTemplate.send(CREATE_ORDER_TOPIC, event.getOrderId().toString(), event);
    }
}
