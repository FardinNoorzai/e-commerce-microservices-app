package com.fardin.orderservice.services;

import com.shopmate.events.OrderEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Profile("kafka")
public class KafkaOrderPublisher implements OrderPublisher {

    @Autowired
    KafkaTemplate<String, OrderEvent> kafkaTemplate;
    @Override
    public void publish(OrderEvent order) {
        order.setTimestamp(Instant.now().toEpochMilli());
        kafkaTemplate.send("orders", order);
    }
}
