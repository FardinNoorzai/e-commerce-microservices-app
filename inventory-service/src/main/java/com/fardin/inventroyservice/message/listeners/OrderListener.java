package com.fardin.inventroyservice.message.listeners;

import com.fardin.inventroyservice.services.InventoryService;
import com.shopmate.events.CheckoutEvent;
import com.shopmate.events.InventoryCalculationEvent;
import com.shopmate.events.InventoryValidationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
@Service
public class OrderListener {
    @Autowired
    InventoryService inventoryService;
    @Autowired
    KafkaTemplate<String, InventoryValidationEvent> kafkaTemplate;


    @KafkaListener(topics = "checkout",groupId = "inventory-service-group")
    public void checkout(CheckoutEvent order) {
        InventoryValidationEvent inventoryValidationEvent = inventoryService.calculateInventory(order);
        kafkaTemplate.send("inventory-validation",inventoryValidationEvent);
    }

}
