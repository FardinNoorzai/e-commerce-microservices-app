package com.fardin.orderservice.services;

import com.shopmate.events.OrderEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Profile("kafka")
public class KafkaOrderPublisher implements OrderPublisher {

    @Autowired
    KafkaTemplate<String, OrderEvent> kafkaTemplate;
    public void publishNewOrder(String topic,OrderEvent order) {

    }

    @Override
    public void publish(OrderEvent order) {
        kafkaTemplate.send("orders", order);
    }
}
