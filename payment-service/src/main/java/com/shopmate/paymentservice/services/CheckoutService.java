package com.shopmate.paymentservice.services;

import com.shopmate.paymentservice.dtos.CheckoutSessionRequest;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    public void init(){
        Stripe.apiKey = stripeKey;
    }

    public ResponseEntity<?> createCheckoutSession(CheckoutSessionRequest request) throws Exception {

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("payment_method_types", List.of("card"));
            params.put("line_items", List.of(Map.of(
                    "price_data", Map.of(
                            "currency", request.getCurrency(),
                            "product_data", Map.of("name", "Pen"),
                            "unit_amount", request.getAmount()
                    ),
                    "quantity", 1
            )));
            params.put("mode", "payment");
            params.put("success_url", request.getSuccessUrl());
            params.put("cancel_url", request.getCancelUrl());

            Session session = Session.create(params);
            return ResponseEntity.ok(Collections.singletonMap("url", session.getUrl()));
        } catch (StripeException e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

}
