package com.fardin.orderservice.dtos;

import lombok.*;

public class OrderStatus {
    String orderId;
    String status;
    String paymentUrl;

    public OrderStatus(String orderId, String status, String paymentUrl) {
        this.orderId = orderId;
        this.status = status;
        this.paymentUrl = paymentUrl;
    }
    public OrderStatus() {

    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    @Override
    public String toString() {
        return "OrderStatus{" +
                "orderId='" + orderId + '\'' +
                ", status='" + status + '\'' +
                ", paymentUrl='" + paymentUrl + '\'' +
                '}';
    }
}
