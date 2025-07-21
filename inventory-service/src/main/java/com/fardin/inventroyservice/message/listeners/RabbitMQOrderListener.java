package com.fardin.inventroyservice.message.listeners;

import com.fardin.inventroyservice.services.InventoryService;
import com.shopmate.events.CheckoutEvent;
import com.shopmate.events.InventoryValidationEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("rabbitmq")
public class RabbitMQOrderListener {
    private static final String EXCHANGE = "inventory-validation.exchange";
    private static final String ROUTING_KEY = "inventory-validation";
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "checkout.queue.inventoryservice")
    public void checkout(CheckoutEvent order) {
        InventoryValidationEvent inventoryValidationEvent = inventoryService.calculateInventory(order);
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, inventoryValidationEvent);
        System.out.println("Sent InventoryValidationEvent to RabbitMQ");
    }
}


