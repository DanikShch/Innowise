package com.innowise.paymentservice.Repository;

import com.innowise.paymentservice.model.Payment;
import com.innowise.paymentservice.model.PaymentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByUserId(Long userId);

    List<Payment> findByOrderId(Long orderId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByTimestampBetween(Instant timestampAfter, Instant timestampBefore);
}
