package com.shopmate.paymentservice.services;

import com.shopmate.events.PaymentCompletedEvent;
import jakarta.persistence.criteria.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Profile("kafka")
public class KafkaPaymentPublisher implements PaymentPublisher{

    @Autowired
    KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate;
    @Override
    public void publish(PaymentCompletedEvent event) {
        event.setTimestamp(Instant.now().toEpochMilli());
        kafkaTemplate.send("completed-payments", event);

    }
}
