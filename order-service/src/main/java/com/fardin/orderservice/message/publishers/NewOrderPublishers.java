package com.fardin.orderservice.message.publishers;

import com.shopmate.dtos.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NewOrderPublishers {

    @Autowired
    KafkaTemplate<String, OrderDto> kafkaTemplate;
    public void publishNewOrder(String topic,OrderDto order) {
        kafkaTemplate.send(topic, order);
    }

}
