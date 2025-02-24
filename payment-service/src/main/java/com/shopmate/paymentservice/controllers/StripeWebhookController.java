package com.shopmate.paymentservice.controllers;

import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/webhook")
public class StripeWebhookController {

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Optional<StripeObject> stripeObject = event.getDataObjectDeserializer().getObject();

            if (stripeObject.isPresent()) {
                Session session = (Session) stripeObject.get();
            } else {
                System.err.println("Failed to deserialize event data for session.completed");
                return ResponseEntity.badRequest().body("Invalid event data");
            }
        }

        return ResponseEntity.ok("Success");
    }

}