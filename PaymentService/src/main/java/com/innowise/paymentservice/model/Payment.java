package com.innowise.paymentservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "payments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {

    @Id
    private String id;

    @Field("order_id")
    private Long orderId;

    @Field("user_id")
    private Long userId;

    private PaymentStatus status;

    private Instant timestamp;

    @Field("payment_amount")
    private BigDecimal paymentAmount;
}
