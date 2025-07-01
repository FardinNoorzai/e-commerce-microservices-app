package com.fardin.orderservice.message.publishers;

import com.shopmate.events.OrderDto;
import com.shopmate.events.OrderEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderPublisher {

    @Autowired
    KafkaTemplate<String, OrderEvent> kafkaTemplate;
    public void publishNewOrder(String topic,OrderEvent order) {
        kafkaTemplate.send("orders", order);
    }

}
