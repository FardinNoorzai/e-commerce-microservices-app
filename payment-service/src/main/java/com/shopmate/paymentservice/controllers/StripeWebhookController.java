package com.shopmate.paymentservice.controllers;

import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.HasId;
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
        System.out.println(sigHeader);

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (Exception e) {
            System.err.println("Webhook signature verification failed: " + e.getMessage());
            return ResponseEntity.badRequest().body("Invalid signature");
        }
        System.out.println(event);
        if ("checkout.session.completed".equals(event.getType())) {
            Optional<StripeObject> stripeObjectOptional = event.getDataObjectDeserializer().getObject();
            Session session = null;

            if (stripeObjectOptional.isPresent()) {
                try {
                    session = (Session) stripeObjectOptional.get();
                } catch (ClassCastException e) {
                    System.err.println("Deserialized object is not of type Session: " + e.getMessage());
                }
            }

            if (session == null) {
                StripeObject rawObject = event.getData().getObject();
                String sessionId = null;
                if (rawObject instanceof HasId) {
                    sessionId = ((HasId) rawObject).getId();
                }
                if (sessionId == null) {
                    System.err.println("No session ID found in event data");
                    return ResponseEntity.badRequest().body("Invalid event data");
                }
                try {
                    session = Session.retrieve(sessionId);
                } catch (Exception e) {
                    System.err.println("Failed to retrieve session from Stripe: " + e.getMessage());
                    return ResponseEntity.badRequest().body("Invalid event data");
                }
            }

            // Process the session as needed
            System.out.println("Session retrieved successfully: " + session.getId());
            System.out.println(session.getPaymentStatus());
        }

        return ResponseEntity.ok("Success");
    }
}
