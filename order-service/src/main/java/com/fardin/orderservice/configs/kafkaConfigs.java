package com.fardin.orderservice.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;

@Configuration
public class kafkaConfigs {

    public NewTopic orderTopic() {
        return new NewTopic("order", 1, (short) 1);
    }

}
