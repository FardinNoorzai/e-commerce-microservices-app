package com.shopmate.paymentservice.services;

import com.shopmate.events.PaymentCompletedEvent;
import com.shopmate.paymentservice.models.Payment;
import com.shopmate.paymentservice.repositories.PaymentRepository;
import com.stripe.model.Event;
import com.stripe.model.HasId;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WebhookService {

    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    PaymentPublisher paymentPublisher;
    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    public ResponseEntity<String> handleWebhook(String payload, String sigHeader) {
        System.out.println("webhook event received");
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (Exception e) {
            System.err.println("Webhook signature verification failed: " + e.getMessage());
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        switch (event.getType()) {
            case "checkout.session.completed":
                System.out.println("handling completed payment");
                handleCheckoutSessionCompleted(event);
                break;
            case "payment_intent.canceled":
                System.out.println("Checkout session canceled");
                break;
            default:
                System.out.println("Unhandled event type: " + event.getType());
        }
        return ResponseEntity.ok("Success");
    }

    private Session retrieveSession(Event event) {
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
                return null;
            }
            try {
                session = Session.retrieve(sessionId);
            } catch (Exception e) {
                System.err.println("Failed to retrieve session from Stripe: " + e.getMessage());
            }
        }
        return session;
    }

    private void handleCheckoutSessionCompleted(Event event) {
        Session session = retrieveSession(event);
        if (session == null) {
            System.err.println("Session retrieval failed.");
            return;
        }

        System.out.println("Session retrieved successfully: " + session.getId());
        System.out.println("Payment status: " + session.getPaymentStatus());

        String email = session.getMetadata() != null ? session.getMetadata().get("email") : null;
        String name = session.getMetadata() != null ? session.getMetadata().get("name") : null;

        Payment payment = findByPaymentId(session.getId());
        if (payment == null) {
            System.err.println("No payment found for session: " + session.getId());
            return;
        }

        PaymentCompletedEvent dto = new PaymentCompletedEvent();
        dto.setPaymentId(session.getId());
        dto.setCheckoutId(payment.getCheckoutId());
        dto.setEmail(email);
        dto.setCustomerName(name);

        if ("paid".equalsIgnoreCase(session.getPaymentStatus())) {
            dto.setStatus("PAID");
            System.out.println("Payment succeeded and event published.");
        } else {
            dto.setStatus("FAILED");
            System.out.println("Payment failed and failure event published.");
        }
        paymentPublisher.publish(dto);
    }


    public Payment findByPaymentId(String id) {
        return paymentRepository.findByPaymentId(id);
    }
}
