package com.shopmate.paymentservice.services;

import com.shopmate.events.OrderEvent;
import com.shopmate.events.PaymentEvent;
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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckoutService {
    @Autowired
    PaymentRepository paymentRepository;
    @Value("${stripe.secret-key}")
    private String stripeKey;
    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;
    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeKey;
    }

    public PaymentEvent createCheckoutSession(OrderEvent request) {
        System.out.println(request);

        if (request.getCartItems() == null || request.getCartItems().isEmpty() || request.getTotalPrice() == null) {
            return null;
        }

        try {
            int amountInCents = request.getTotalPrice()
                    .multiply(BigDecimal.valueOf(100))
                    .intValueExact();

            Map<String, Object> params = new HashMap<>();
            params.put("payment_method_types", List.of("card"));

            String productName = request.getCartItems().size() + " product" +
                    (request.getCartItems().size() > 1 ? "s" : "");

            Map<String, Object> priceData = Map.of(
                    "currency", "USD",
                    "product_data", Map.of("name", productName),
                    "unit_amount", amountInCents
            );

            Map<String, Object> lineItem = Map.of(
                    "price_data", priceData,
                    "quantity", 1
            );

            params.put("line_items", List.of(lineItem));
            params.put("mode", "payment");
            params.put("success_url", successUrl);
            params.put("cancel_url", cancelUrl);

            Session session = Session.create(params);

            PaymentEvent paymentDto = new PaymentEvent();
            paymentDto.setPaymentId(session.getId());
            paymentDto.setUri(session.getUrl());
            paymentDto.setCheckoutId(request.getCheckoutId());
            Payment payment = new Payment();
            payment.setPaymentId(session.getId());
            payment.setOrderId(request.getCheckoutId());
            payment.setCheckoutId(request.getCheckoutId());

            paymentRepository.save(payment);

            return paymentDto;
        } catch (StripeException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
