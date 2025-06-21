package com.fardin.orderservice.message.listeners;

import com.fardin.orderservice.feign.clients.PaymentServiceClient;
import com.fardin.orderservice.message.publishers.PaymentSuccessPublisher;
import com.fardin.orderservice.models.Order;
import com.fardin.orderservice.services.OrderService;
import com.fardin.orderservice.states.OrderStatus;
import com.shopmate.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentListener {
    @Autowired
    OrderService orderService;
    @Autowired
    PaymentSuccessPublisher paymentSuccessPublisher;

    @KafkaListener(topics = "completed-payments",groupId = "order-service-group")
    public void listen(CompletedPaymentDto message) {
        Order order = orderService.updateOrderByPaymentCompletion(message);
        PaymentSuccessDto paymentSuccessDto = new PaymentSuccessDto();
        AddressDto addressDto = new AddressDto();
        addressDto.setCity(order.getShippingAddress().getCity());
        addressDto.setState(order.getShippingAddress().getState());
        addressDto.setZipCode(order.getShippingAddress().getZipCode());
        addressDto.setStreet(order.getShippingAddress().getStreet());
        paymentSuccessDto.setAddress(addressDto);
        paymentSuccessDto.setOrderId(order.getId());
        paymentSuccessDto.setPrice(order.getPrice());
        paymentSuccessDto.setQuantity(order.getQuantity());
        paymentSuccessDto.setUsername(order.getUsername());
        paymentSuccessPublisher.publishNewOrder("payment-success",paymentSuccessDto);
    }

}
