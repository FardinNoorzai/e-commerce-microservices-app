package com.fardin.orderservice.message.listeners;

import com.fardin.orderservice.feign.clients.PaymentServiceClient;
import com.fardin.orderservice.models.Order;
import com.fardin.orderservice.services.OrderService;
import com.fardin.orderservice.states.OrderStatus;
import com.shopmate.dtos.CheckoutSessionRequestDto;
import com.shopmate.dtos.InventoryResponseToNewOrderDto;
import com.shopmate.dtos.PaymentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryListener {
    @Autowired
    OrderService orderService;

    @Autowired
    PaymentServiceClient paymentServiceClient;


    @KafkaListener(topics = "inventory",groupId = "order-service-group")
    public void listen(InventoryResponseToNewOrderDto message) {
        System.out.println("Received new order");
        Order order = orderService.updateOrderStatusByInventoryResponse(message);
        if(order != null) {
            if(order.getStatus().equals(OrderStatus.PAYMENT_PENDING)){
                CheckoutSessionRequestDto dto = orderService.createCheckoutSessionObject(order);
                System.out.println(dto.getAmount());
                System.out.println(dto.getPrice());
                PaymentDto paymentDto = paymentServiceClient.getSession(dto);
                orderService.updateOrderStatusByPaymentResponse(paymentDto);
            }
        }
    }

}
