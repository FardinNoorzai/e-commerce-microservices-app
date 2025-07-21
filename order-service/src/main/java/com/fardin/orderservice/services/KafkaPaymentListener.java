package com.fardin.orderservice.services;

import com.shopmate.events.PaymentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Profile("kafka")
public class KafkaPaymentListener {
    @Autowired
    private OrderService orderService;

    @KafkaListener(topics = "payments",groupId = "order-service-group")
    public void listen(PaymentEvent event) throws Exception {
        System.out.println("Received Payment Event " +  event.toString());
        orderService.updateOrderWithPaymentInformation(event);
    }

}
