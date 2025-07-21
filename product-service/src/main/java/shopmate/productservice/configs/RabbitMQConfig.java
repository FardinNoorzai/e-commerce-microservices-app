package shopmate.productservice.configs;

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
    public static final String QUEUE = "checkout.queue.productservice";
    public static final String ROUTING_KEY = "checkout";

    @Bean
    public TopicExchange checkoutExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue productQueue() {
        return new Queue(QUEUE);
    }

    @Bean
    public Binding productBinding(Queue productQueue, TopicExchange checkoutExchange) {
        return BindingBuilder.bind(productQueue).to(checkoutExchange).with(ROUTING_KEY);
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
    public TopicExchange productValidationExchange() {
        return new TopicExchange("product-validation.exchange");
    }

    @Bean
    public Queue productValidationQueue() {
        return new Queue("product-validation.queue");
    }

    @Bean
    public Binding productValidationBinding(Queue productValidationQueue, TopicExchange productValidationExchange) {
        return BindingBuilder.bind(productValidationQueue).to(productValidationExchange).with("product-validation");
    }


}
