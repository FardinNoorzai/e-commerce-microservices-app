package com.shopmate.cartservice.services;

import com.shopmate.cartservice.models.Checkout;
import com.shopmate.cartservice.models.CheckoutStatus;
import com.shopmate.cartservice.repositories.CheckoutRepository;
import com.shopmate.events.OrderEvent;
import com.shopmate.events.PaymentCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Profile("kafka")
public class KafkaEventListeners {

    @Autowired
    CheckoutRepository checkoutRepository;
    Logger log = LoggerFactory.getLogger(KafkaEventListeners.class);

    @KafkaListener(topics = "orders", groupId = "cart-service-group")
    public void onNewOrder(OrderEvent orderEvent) {
        Checkout checkout = checkoutRepository.findById(orderEvent.getCheckoutId()).orElse(null);

        if (checkout == null) {
            log.error("❌ Checkout not found for ID: {}", orderEvent.getCheckoutId());
            return; // or throw new IllegalStateException("Missing checkout");
        }

        if (orderEvent.getStatus().equals(OrderEvent.OrderStatus.VALID)) {
            checkout.setStatus(CheckoutStatus.PENDING_PAYMENT);
        } else if (orderEvent.getStatus().equals(OrderEvent.OrderStatus.FAILED)) {
            checkout.setStatus(CheckoutStatus.FAILED);
        }

        checkoutRepository.save(checkout);
    }

    @KafkaListener(topics = "completed-payments",groupId = "cart-service-group")
    public void onNewPayment(PaymentCompletedEvent event) {
        Checkout checkout = checkoutRepository.findById(event.getCheckoutId()).orElse(null);
        if(event.getStatus().equalsIgnoreCase("PAID")){
            if(checkout != null){
                checkout.setStatus(CheckoutStatus.SHIPPING);
            }
        }else {
            checkout.setStatus(CheckoutStatus.FAILED);
        }
        checkoutRepository.save(checkout);
    }

}
