package com.shopmate.paymentservice.message.listeners;

import com.shopmate.events.OrderEvent;
import com.shopmate.events.PaymentEvent;
import com.shopmate.paymentservice.services.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderListener {
    @Autowired
    CheckoutService checkoutService;

    @Autowired
    KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @KafkaListener(topics = "orders", groupId = "payment-group-id")
    public void listen(OrderEvent orderEvent) throws Exception {
        PaymentEvent event = checkoutService.createCheckoutSession(orderEvent);
        kafkaTemplate.send("payments", event);
    }

}
