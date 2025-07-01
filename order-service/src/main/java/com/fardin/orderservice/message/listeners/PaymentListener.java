package com.fardin.orderservice.message.listeners;

import com.fardin.orderservice.services.OrderService;
import com.shopmate.events.OrderEvent;
import com.shopmate.events.PaymentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentListener {
    @Autowired
    private OrderService orderService;

    @KafkaListener(topics = "payments",groupId = "order-service-group")
    public void listen(PaymentEvent event) throws Exception {
        System.out.println("Received Payment Event " +  event.toString());
        orderService.updateOrderWithPaymentInformation(event);
    }

}
