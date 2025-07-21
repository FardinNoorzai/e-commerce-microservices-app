package com.shopmate.cartservice.services;

import com.shopmate.cartservice.models.CheckoutStatus;
import com.shopmate.events.OrderEvent;
import com.shopmate.events.PaymentCompletedEvent;
import com.shopmate.cartservice.models.Checkout;
import com.shopmate.cartservice.repositories.CheckoutRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("rabbitmq")
public class RabbitEventListeners {

    @Autowired
    private CheckoutRepository checkoutRepository;

    @RabbitListener(queues = "orders.queue.cartservice")
    public void onNewOrder(OrderEvent orderEvent) {
        Checkout checkout = checkoutRepository.findById(orderEvent.getCheckoutId()).orElse(null);
        if (orderEvent.getStatus() == OrderEvent.OrderStatus.VALID) {
            if (checkout != null) {
                checkout.setStatus(CheckoutStatus.PENDING_PAYMENT);
            }
        } else if (orderEvent.getStatus() == OrderEvent.OrderStatus.FAILED) {
            if (checkout != null) {
                checkout.setStatus(CheckoutStatus.FAILED);
            }
        }
        if (checkout != null) {
            checkoutRepository.save(checkout);
        }
    }

    @RabbitListener(queues = "completed-payments.queue.cartservice")
    public void onNewPayment(PaymentCompletedEvent event) {
        Checkout checkout = checkoutRepository.findById(event.getCheckoutId()).orElse(null);
        if (event.getStatus().equalsIgnoreCase("PAID")) {
            if (checkout != null) {
                checkout.setStatus(CheckoutStatus.SHIPPING);
            }
        } else {
            if (checkout != null) {
                checkout.setStatus(CheckoutStatus.FAILED);
            }
        }
        if (checkout != null) {
            checkoutRepository.save(checkout);
        }
    }
}
