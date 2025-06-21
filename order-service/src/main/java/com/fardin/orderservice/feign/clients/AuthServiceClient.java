package com.fardin.orderservice.feign.clients;

import com.fardin.orderservice.dtos.AuthRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("user-service")
public interface AuthServiceClient {
    @PostMapping("api/users/auth/login")
    public String getToken(@RequestBody AuthRequest authRequest);
}
