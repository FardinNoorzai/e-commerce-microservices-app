package com.shopmate.paymentservice.services;

import com.shopmate.events.PaymentCompletedEvent;
import jakarta.persistence.criteria.Order;

public interface PaymentPublisher {
    public void publish(PaymentCompletedEvent event);
}
