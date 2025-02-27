package com.shopmate.paymentservice.services;

import com.shopmate.dtos.CheckoutSessionRequestDto;
import com.shopmate.dtos.PaymentDto;
import com.shopmate.paymentservice.models.Payment;
import com.shopmate.paymentservice.repositories.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckoutService {
    @Value("${stripe.secret-key}")
    private String stripeKey;
    @Autowired
    PaymentRepository paymentRepository;

    @PostConstruct
    public void init(){
        Stripe.apiKey = stripeKey;
    }

    public ResponseEntity<PaymentDto> createCheckoutSession(CheckoutSessionRequestDto request) throws Exception {
        System.out.println(request);
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("payment_method_types", List.of("card"));
            params.put("line_items", List.of(Map.of(
                    "price_data", Map.of(
                            "currency", request.getCurrency(),
                            "product_data", Map.of("name", request.getProduct()),
                            "unit_amount", request.getPrice().intValueExact()
                    ),
                    "quantity", request.getAmount().intValueExact()
            )));
            params.put("mode", "payment");
            params.put("success_url", request.getSuccessUrl());
            params.put("cancel_url", request.getCancelUrl());

            Session session = Session.create(params);
            PaymentDto paymentDto = new PaymentDto();
            paymentDto.setPaymentId(session.getId());
            paymentDto.setUri(session.getUrl());
            paymentDto.setOrderId(request.getOrderId());
            Payment payment = new Payment();
            payment.setPaymentId(session.getId());
            payment.setOrderId(request.getOrderId());
            paymentRepository.save(payment);
            return ResponseEntity.ok(paymentDto);
        } catch (StripeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(new PaymentDto());
        }
    }

}
