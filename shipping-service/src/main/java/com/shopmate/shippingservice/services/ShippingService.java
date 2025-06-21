package com.shopmate.shippingservice.services;

import com.shopmate.dtos.PaymentSuccessDto;
import com.shopmate.shippingservice.models.Address;
import com.shopmate.shippingservice.models.Ship;
import com.shopmate.shippingservice.models.Status;
import com.shopmate.shippingservice.repositories.ShippingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShippingService {
    @Autowired
    ShippingRepository shippingRepository;

    public void create(PaymentSuccessDto message) {
        Ship ship = new Ship();
        Address address = new Address();
        address.setCity(message.getAddress().getCity());
        address.setState(message.getAddress().getState());
        address.setZipCode(message.getAddress().getZipCode());
        address.setStreet(message.getAddress().getStreet());
        ship.setStatus(Status.SHIPPING);
        ship.setShippingAddress(address);
        ship.setUsername(message.getUsername());
        ship.setOrderId(message.getOrderId());
        shippingRepository.save(ship);
    }
}
