package com.innowise.orderservice.kafka.consumer;

import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.kafka.event.CreatePaymentEvent;
import com.innowise.orderservice.model.Order;
import com.innowise.orderservice.model.OrderStatus;
import com.innowise.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "${app.kafka.create-payment-topic}", groupId = "${app.kafka.order-group-id}")
    public void handle(CreatePaymentEvent event) {

        log.info("Received CREATE_PAYMENT event: {}", event);

        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(event.getOrderId()));

        if ("SUCCESS".equals(event.getStatus())) {
            order.setStatus(OrderStatus.PAID);
        } else {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
        }

        orderRepository.save(order);

        log.info("Order {} updated with status {}", order.getId(), order.getStatus());
    }
}

