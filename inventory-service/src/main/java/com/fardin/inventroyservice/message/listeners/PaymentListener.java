package com.fardin.inventroyservice.message.listeners;

import com.fardin.inventroyservice.services.InventoryService;
import com.shopmate.events.PaymentSuccessDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Profile("kafka")
public class PaymentListener {

    @Autowired
    InventoryService inventoryService;
    @KafkaListener(topics = "payment-success",groupId = "inventory-service-group")
    public void listen(PaymentSuccessDto message) {
        inventoryService.updateOrderStatus(message);
    }

}
