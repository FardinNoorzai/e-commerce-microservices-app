package com.fardin.orderservice.message.listeners;

import com.fardin.orderservice.services.OrderService;
import com.shopmate.dtos.InventoryResponseToNewOrderDto;
import com.shopmate.dtos.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryListener {
    @Autowired
    OrderService orderService;

    @KafkaListener(topics = "inventory",groupId = "order-service-group")
    public void listen(InventoryResponseToNewOrderDto message) {
        orderService.updateOrderStatus(message);
    }

}
