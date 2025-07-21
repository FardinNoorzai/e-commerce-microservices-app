package com.fardin.orderservice.configs;

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
    @Bean
    public TopicExchange paymentsExchange() {
        return new TopicExchange("payments.exchange");
    }

    @Bean
    public Queue paymentsQueueOrderService() {
        return new Queue("payments.queue.orderservice");
    }

    @Bean
    public Binding bindPaymentsQueue() {
        return BindingBuilder
                .bind(paymentsQueueOrderService())
                .to(paymentsExchange())
                .with("payments");
    }

    @Bean
    public TopicExchange checkoutExchange() {
        return new TopicExchange("checkout.exchange");
    }

    @Bean
    public TopicExchange userValidationExchange() {
        return new TopicExchange("user-validation.exchange");
    }

    @Bean
    public TopicExchange productValidationExchange() {
        return new TopicExchange("product-validation.exchange");
    }

    @Bean
    public TopicExchange inventoryValidationExchange() {
        return new TopicExchange("inventory-validation.exchange");
    }

    @Bean
    public Queue checkoutQueue() {
        return new Queue("checkout.queue.orderservice");
    }

    @Bean
    public Queue userValidationQueue() {
        return new Queue("user-validation.queue.orderservice");
    }

    @Bean
    public Queue productValidationQueue() {
        return new Queue("product-validation.queue.orderservice");
    }

    @Bean
    public Queue inventoryValidationQueue() {
        return new Queue("inventory-validation.queue.orderservice");
    }

    @Bean
    public Binding bindCheckout() {
        return BindingBuilder.bind(checkoutQueue()).to(checkoutExchange()).with("checkout");
    }

    @Bean
    public Binding bindUserValidation() {
        return BindingBuilder.bind(userValidationQueue()).to(userValidationExchange()).with("user-validation");
    }

    @Bean
    public Binding bindProductValidation() {
        return BindingBuilder.bind(productValidationQueue()).to(productValidationExchange()).with("product-validation");
    }

    @Bean
    public Binding bindInventoryValidation() {
        return BindingBuilder.bind(inventoryValidationQueue()).to(inventoryValidationExchange()).with("inventory-validation");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange("orders.exchange");
    }

    @Bean
    public Queue orderQueue() {
        return new Queue("orders.queue");
    }

    @Bean
    public Binding orderBinding(Queue orderQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with("orders");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
