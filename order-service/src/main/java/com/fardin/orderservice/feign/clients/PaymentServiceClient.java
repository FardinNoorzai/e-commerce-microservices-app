package com.fardin.orderservice.feign.clients;

import com.shopmate.dtos.CheckoutSessionRequestDto;
import com.shopmate.dtos.PaymentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("payment-service")
public interface PaymentServiceClient {
    @PostMapping("api/payments/create-checkout-session")
    public PaymentDto getSession(@RequestBody CheckoutSessionRequestDto checkoutSessionRequestDto);
}
