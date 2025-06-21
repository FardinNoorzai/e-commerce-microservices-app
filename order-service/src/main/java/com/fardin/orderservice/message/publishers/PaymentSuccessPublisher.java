package com.fardin.orderservice.message.publishers;

import com.shopmate.dtos.OrderDto;
import com.shopmate.dtos.PaymentSuccessDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service

public class PaymentSuccessPublisher {

    @Autowired
    KafkaTemplate<String, PaymentSuccessDto> kafkaTemplate;
    public void publishNewOrder(String topic, PaymentSuccessDto order) {
        kafkaTemplate.send(topic, order);
    }
}
