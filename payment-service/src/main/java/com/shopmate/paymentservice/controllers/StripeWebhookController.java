package com.shopmate.paymentservice.controllers;

import com.shopmate.paymentservice.services.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class StripeWebhookController {

    @Autowired
    WebhookService webhookService;

    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        return webhookService.handleWebhook(payload,sigHeader);
    }
}
