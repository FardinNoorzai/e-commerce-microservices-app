package com.fardin.inventroyservice.message.listeners;

import com.fardin.inventroyservice.services.InventoryService;
import com.shopmate.events.CheckoutEvent;
import com.shopmate.events.InventoryValidationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Profile("kafka")
public class KafkaOrderListener {
    @Autowired
    InventoryService inventoryService;
    @Autowired
    KafkaTemplate<String, InventoryValidationEvent> kafkaTemplate;


    @KafkaListener(topics = "checkout",groupId = "inventory-service-group")
    public void checkout(CheckoutEvent order) {
        InventoryValidationEvent inventoryValidationEvent = inventoryService.randomCheck(order);
        inventoryValidationEvent.setTimestamp(Instant.now().toEpochMilli());
        kafkaTemplate.send("inventory-validation",inventoryValidationEvent);
    }

}
