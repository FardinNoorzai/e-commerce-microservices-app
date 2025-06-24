package com.shopmate.cartservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfigs {
    @Bean
    public NewTopic checkoutTopic() {
        return new NewTopic("checkout", 1, (short) 1);
    }

}
