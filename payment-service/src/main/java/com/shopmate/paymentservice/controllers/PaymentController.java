package com.shopmate.paymentservice.controllers;

import com.shopmate.paymentservice.services.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    CheckoutService checkoutService;

//    @PostMapping("/create-checkout-session")
//    public ResponseEntity<PaymentEvent> createCheckoutSession(@RequestBody CheckoutSessionRequestDto request) throws Exception {
//        return checkoutService.createCheckoutSession(request);
//    }

}
