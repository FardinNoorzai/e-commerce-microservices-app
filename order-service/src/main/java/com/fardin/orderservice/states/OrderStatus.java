package com.fardin.orderservice.states;

public enum OrderStatus {
    PENDING,
    PAYMENT_PENDING,
    PAYMENT_SUCCESS,
    SHIPPING,
    SHIPPED_SUCCESS,
    REJECTED_BY_INVENTORY,
    REJECTED_BY_PAYMENT,
}
