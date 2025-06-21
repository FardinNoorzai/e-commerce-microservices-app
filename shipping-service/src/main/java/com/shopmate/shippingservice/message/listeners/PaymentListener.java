package com.shopmate.shippingservice.message.listeners;

import com.shopmate.dtos.CompletedPaymentDto;
import com.shopmate.dtos.PaymentSuccessDto;
import com.shopmate.shippingservice.models.Address;
import com.shopmate.shippingservice.models.Ship;
import com.shopmate.shippingservice.models.Status;
import com.shopmate.shippingservice.services.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentListener {

    @Autowired
    ShippingService shippingService;
    @KafkaListener(topics = "payment-success",groupId = "shipping-service-group")
    public void listen(PaymentSuccessDto message) {
        System.out.println("payment-success was received");
       shippingService.create(message);
    }

}
