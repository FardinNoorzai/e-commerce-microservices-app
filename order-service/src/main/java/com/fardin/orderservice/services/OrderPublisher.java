package com.fardin.orderservice.services;

import com.fardin.orderservice.models.Order;
import com.shopmate.events.OrderEvent;

public interface OrderPublisher {
    public void publish(OrderEvent order);
}
