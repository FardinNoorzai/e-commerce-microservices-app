package com.fardin.inventroyservice.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("kafka")
public class KafkaConfigs {
    public static final String INVENTORY_TOPIC = "inventory";
    public NewTopic inventoryTopic() {
        return new NewTopic(INVENTORY_TOPIC, 1, (short) 1);
    }
}
