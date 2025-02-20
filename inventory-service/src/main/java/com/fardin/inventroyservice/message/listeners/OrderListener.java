package com.fardin.inventroyservice.message.listeners;

import com.shopmate.OrderDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderListener {

    @KafkaListener(topics = "order",groupId = "inventory-service-group")
    public void listen(OrderDto message) {
        System.out.println(message);
    }

}
