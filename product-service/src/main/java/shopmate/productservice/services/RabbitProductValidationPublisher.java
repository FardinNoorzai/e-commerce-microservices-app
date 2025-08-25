package shopmate.productservice.services;


import com.shopmate.events.ProductValidationEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Profile("rabbitmq")
public class RabbitProductValidationPublisher {

    private static final String EXCHANGE = "product-validation.exchange";
    private static final String ROUTING_KEY = "product-validation";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publish(ProductValidationEvent productValidationEvent) {
        productValidationEvent.setTimestamp(Instant.now().toEpochMilli());
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, productValidationEvent);
        System.out.println("✅ Published ProductValidationEvent to RabbitMQ");
    }
}
