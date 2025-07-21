package com.fardin.inventroyservice.configs;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("rabbitmq")
public class RabbitMQConfig {

    public static final String EXCHANGE = "checkout.exchange";
    public static final String QUEUE = "checkout.queue.inventoryservice";
    public static final String ROUTING_KEY = "checkout";

    @Bean
    public TopicExchange checkoutExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue inventoryQueue() {
        return new Queue(QUEUE);
    }

    @Bean
    public Binding inventoryBinding(Queue inventoryQueue, TopicExchange checkoutExchange) {
        return BindingBuilder.bind(inventoryQueue).to(checkoutExchange).with(ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public TopicExchange inventoryValidationExchange() {
        return new TopicExchange("inventory-validation.exchange");
    }

    @Bean
    public Queue inventoryValidationQueue() {
        return new Queue("inventory-validation.queue");
    }

    @Bean
    public Binding inventoryValidationBinding(Queue inventoryValidationQueue, TopicExchange inventoryValidationExchange) {
        return BindingBuilder.bind(inventoryValidationQueue).to(inventoryValidationExchange).with("inventory-validation");
    }
}
