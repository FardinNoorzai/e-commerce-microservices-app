package com.shopmate.paymentservice.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("kafka")
public class kafkaConfigs {

    public NewTopic orderTopic() {
        return new NewTopic("completed-payments", 1, (short) 1);
    }

}
