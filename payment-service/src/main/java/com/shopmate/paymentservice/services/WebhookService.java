package com.shopmate.paymentservice.services;

import com.shopmate.dtos.CompletedPaymentDto;
import com.shopmate.paymentservice.message.publishers.NewPaymentPublisher;
import com.shopmate.paymentservice.models.Payment;
import com.shopmate.paymentservice.repositories.PaymentRepository;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class WebhookService {

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    NewPaymentPublisher paymentPublisher;

    public ResponseEntity<String> handleWebhook(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (Exception e) {
            System.err.println("Webhook signature verification failed: " + e.getMessage());
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        switch (event.getType()) {
            case "checkout.session.completed":
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

        String customerId = session.getCustomer();
        if (customerId != null) {
            try {
                Customer customer = Customer.retrieve(customerId);
                System.out.println("Customer Email: " + customer.getEmail());
                System.out.println("Customer Name: " + customer.getName());
            } catch (Exception e) {
                System.err.println("Failed to retrieve customer: " + e.getMessage());
            }
        }

        String paymentStatus = session.getPaymentStatus();
        switch (paymentStatus) {
            case "paid":
                handlePaymentSucceeded(session);
                break;
            case "unpaid":
                handlePaymentFailed(session);
                break;
            case "canceled":
                handlePaymentCanceled(session);
                break;
            default:
                System.out.println("Unhandled payment status: " + paymentStatus);
        }
    }

    private void handlePaymentSucceeded(Session session) {
        System.out.println("Handling payment succeeded for session: " + session.getId());
        Payment payment = findByPaymentId(session.getId());
        if (payment == null) {
            System.err.println("No payment found for session: " + session.getId());
            return;
        }
        CompletedPaymentDto completedPaymentDto = new CompletedPaymentDto();
        completedPaymentDto.setPaymentId(session.getId());
        completedPaymentDto.setOrderId(payment.getOrderId());
        completedPaymentDto.setEmail(session.getMetadata().get("email"));
        completedPaymentDto.setCustomerName(session.getMetadata().get("name"));
        completedPaymentDto.setStatus("PAID");
        paymentPublisher.publishNewPayment("completed-payments", completedPaymentDto);
    }

    private void handlePaymentFailed(Session session) {
        System.out.println("Handling payment failed for session: " + session.getId());
    }

    private void handlePaymentCanceled(Session session) {
        System.out.println("Handling payment canceled for session: " + session.getId());
    }


    public Payment findByPaymentId(String id){
        return paymentRepository.findByPaymentId(id);
    }
}
