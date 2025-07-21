package com.shopmate.cartservice.services;

import com.shopmate.events.CheckoutEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Profile("kafka")
public class KafkaPublisher implements CheckoutPublisher {
    @Autowired
    private KafkaTemplate<String, CheckoutEvent> kafkaTemplate;


    @Override
    public void publish(CheckoutEvent checkoutEvent) {
        CompletableFuture<SendResult<String, CheckoutEvent>> future = kafkaTemplate.send("checkout", checkoutEvent);
        future.thenAccept(result -> {
            System.out.println("✅ Sent to Kafka:");
            System.out.println("    Topic: " + result.getRecordMetadata().topic());
            System.out.println("    Partition: " + result.getRecordMetadata().partition());
            System.out.println("    Offset: " + result.getRecordMetadata().offset());
        }).exceptionally(ex -> {
            System.err.println("❌ Failed to send to Kafka: " + ex.getMessage());
            return null;
        });
    }
}
