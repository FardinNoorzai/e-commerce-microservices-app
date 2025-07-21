package com.shopmate.cartservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("rabbitmq")
public class RabbitMQConfig {

    public static final String EXCHANGE = "checkout.exchange";
    public static final String QUEUE = "checkout.queue";
    public static final String ROUTING_KEY = "checkout";

    @Bean
    public TopicExchange checkoutExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue checkoutQueue() {
        return new Queue(QUEUE);
    }

    @Bean
    public Binding checkoutBinding(Queue checkoutQueue, TopicExchange checkoutExchange) {
        return BindingBuilder.bind(checkoutQueue).to(checkoutExchange).with(ROUTING_KEY);
    }
    @Bean
    public TopicExchange ordersExchange() {
        return new TopicExchange("orders.exchange");
    }

    @Bean
    public Queue ordersQueueCartService() {
        return new Queue("orders.queue.cartservice");
    }

    @Bean
    public Binding bindOrdersQueue() {
        return BindingBuilder
                .bind(ordersQueueCartService())
                .to(ordersExchange())
                .with("orders");
    }

    @Bean
    public TopicExchange completedPaymentsExchange() {
        return new TopicExchange("completed-payments.exchange");
    }

    @Bean
    public Queue completedPaymentsQueueCartService() {
        return new Queue("completed-payments.queue.cartservice");
    }

    @Bean
    public Binding bindCompletedPaymentsQueue() {
        return BindingBuilder
                .bind(completedPaymentsQueueCartService())
                .to(completedPaymentsExchange())
                .with("completed-payments");
    }

}
