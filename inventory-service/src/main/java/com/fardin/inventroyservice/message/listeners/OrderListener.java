package com.fardin.inventroyservice.message.listeners;

import com.fardin.inventroyservice.services.InventoryService;
import com.shopmate.dtos.InventoryResponseToNewOrderDto;
import com.shopmate.dtos.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderListener {
    @Autowired
    InventoryService inventoryService;
    @Autowired
    KafkaTemplate<String, InventoryResponseToNewOrderDto> kafkaTemplate;
    @KafkaListener(topics = "order",groupId = "inventory-service-group")
    public void listen(OrderDto message) {
        System.out.println("Received order: " + message);
        InventoryResponseToNewOrderDto response = inventoryService.calculateInventory(message);
        System.out.println(response);
        System.out.println("publishing to kafka");
        kafkaTemplate.send("inventory", response);
    }

}
