package com.shopmate.paymentservice.message.publishers;

import com.shopmate.events.CompletedPaymentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NewPaymentPublisher {

    @Autowired
    KafkaTemplate<String, CompletedPaymentDto> kafkaTemplate;
    public void publishNewPayment(String topic,CompletedPaymentDto order) {
        kafkaTemplate.send(topic, order);
    }

}
