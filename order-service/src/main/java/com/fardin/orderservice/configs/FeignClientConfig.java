package com.fardin.orderservice.configs;

import com.fardin.orderservice.services.JwtTokenService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class FeignClientConfig implements RequestInterceptor {
    @Autowired
    JwtTokenService jwtTokenService;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("Authorization", "Bearer " + jwtTokenService.getToken());
    }

}
