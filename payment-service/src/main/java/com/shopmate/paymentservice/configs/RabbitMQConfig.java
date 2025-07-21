package com.shopmate.paymentservice.configs;

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
    public TopicExchange ordersExchange() {
        return new TopicExchange("orders.exchange");
    }

    @Bean
    public Queue ordersQueuePaymentService() {
        return new Queue("orders.queue.paymentservice");
    }

    @Bean
    public Binding bindOrdersQueue() {
        return BindingBuilder
                .bind(ordersQueuePaymentService())
                .to(ordersExchange())
                .with("orders");
    }

    @Bean
    public TopicExchange paymentsExchange() {
        return new TopicExchange("payments.exchange");
    }

    @Bean
    public Queue paymentsQueue() {
        return new Queue("payments.queue");
    }

    @Bean
    public Binding bindPaymentsQueue() {
        return BindingBuilder
                .bind(paymentsQueue())
                .to(paymentsExchange())
                .with("payments");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
