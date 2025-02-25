package com.fardin.orderservice.message.listeners;

import com.fardin.orderservice.feign.clients.PaymentServiceClient;
import com.fardin.orderservice.models.Order;
import com.fardin.orderservice.services.OrderService;
import com.fardin.orderservice.states.OrderStatus;
import com.shopmate.dtos.CheckoutSessionRequestDto;
import com.shopmate.dtos.CompletedPaymentDto;
import com.shopmate.dtos.InventoryResponseToNewOrderDto;
import com.shopmate.dtos.PaymentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentListener {
    @Autowired
    OrderService orderService;

    @KafkaListener(topics = "completed-payments",groupId = "order-service-group")
    public void listen(CompletedPaymentDto message) {
        System.out.println("Received completed payment " + message.getOrderId());

    }

}
