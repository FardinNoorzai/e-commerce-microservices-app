package com.fardin.inventroyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class InventroyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventroyServiceApplication.class, args);
    }

}
