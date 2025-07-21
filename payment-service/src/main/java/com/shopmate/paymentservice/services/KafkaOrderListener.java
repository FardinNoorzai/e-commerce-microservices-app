package com.shopmate.paymentservice.services;

import com.shopmate.events.OrderEvent;
import com.shopmate.events.PaymentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Profile("kafka")
public class KafkaOrderListener {
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
