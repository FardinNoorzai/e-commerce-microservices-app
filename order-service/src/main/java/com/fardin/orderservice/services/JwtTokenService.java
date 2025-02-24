package com.fardin.orderservice.services;

import com.fardin.orderservice.dtos.AuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class JwtTokenService {
    @Value("${order.service.password}")
    private String password;
    @Value("${order.service.username}")
    private String username;

    @Autowired
    AuthServiceClient authServiceClient;
    String token = "";

    @Scheduled(fixedRate = 21000000)
    public void init(){
        AuthRequest authRequest = new AuthRequest();
        authRequest.setPassword(password);
        authRequest.setUsername(username);
        token = authServiceClient.getToken(authRequest);
        System.out.println(token);
    }

    public String getToken() {
        return token;
    }
}
