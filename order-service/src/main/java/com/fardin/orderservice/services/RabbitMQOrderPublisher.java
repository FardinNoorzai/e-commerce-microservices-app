package com.fardin.orderservice.services;

import com.shopmate.events.OrderEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("rabbitmq")
public class RabbitMQOrderPublisher implements OrderPublisher {

    private static final String EXCHANGE = "orders.exchange";
    private static final String ROUTING_KEY = "orders";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void publish(OrderEvent order) {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, order);
    }
}
