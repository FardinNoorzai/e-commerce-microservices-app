package com.fardin.orderservice.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("kafka")
public class kafkaConfigs {

    public NewTopic orderTopic() {
        return new NewTopic("order", 1, (short) 1);
    }
    public NewTopic PaymentSuccessTopic() {
        return new NewTopic("payment-success", 1, (short) 1);
    }

}
