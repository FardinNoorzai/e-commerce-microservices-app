package com.shopmate.cartservice.services;

import com.shopmate.events.CheckoutEvent;

public interface CheckoutPublisher {
    public void publish(CheckoutEvent checkoutEvent);
}
