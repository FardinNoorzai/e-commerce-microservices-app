package com.shopmate.cartservice.services;

import com.shopmate.events.CheckoutEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("rabbitmq")
public class RabbitPublisher implements CheckoutPublisher {

    private static final String EXCHANGE_NAME = "checkout.exchange";
    private static final String ROUTING_KEY = "checkout";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void publish(CheckoutEvent checkoutEvent) {
        try {
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, checkoutEvent);
            System.out.println("✅ Sent to RabbitMQ: Exchange = " + EXCHANGE_NAME + ", Routing Key = " + ROUTING_KEY);
        } catch (Exception e) {
            System.err.println("❌ Failed to send to RabbitMQ: " + e.getMessage());
        }
    }
}
