package com.shopmate.paymentservice.services;


import com.shopmate.events.PaymentCompletedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("rabbitmq")
public class RabbitPaymentPublisher implements PaymentPublisher {

    private static final String EXCHANGE = "completed-payments.exchange";
    private static final String ROUTING_KEY = "completed-payments";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void publish(PaymentCompletedEvent event) {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, event);
    }
}
