package com.fardin.orderservice.dtos;

import com.shopmate.events.OrderEvent;
import lombok.*;

public class OrderStatus {
    String checkoutId;
    String orderId;
    String paymentUrl;
    String orderStatus;

    public String getCheckoutId() {
        return checkoutId;
    }

    public void setCheckoutId(String checkoutId) {
        this.checkoutId = checkoutId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public String toString() {
        return "OrderStatus{" +
                "checkoutId='" + checkoutId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", paymentUrl='" + paymentUrl + '\'' +
                ", orderStatus=" + orderStatus +
                '}';
    }
}
