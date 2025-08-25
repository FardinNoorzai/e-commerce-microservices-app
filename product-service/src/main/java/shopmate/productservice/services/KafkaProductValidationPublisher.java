package shopmate.productservice.services;

import com.shopmate.events.ProductValidationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Profile("kafka")
public class KafkaProductValidationPublisher {

    @Autowired
    KafkaTemplate<String, ProductValidationEvent> kafkaTemplate;

    public void publish(ProductValidationEvent productValidationEvent) {
        productValidationEvent.setTimestamp(Instant.now().toEpochMilli());
        kafkaTemplate.send("product-validation", productValidationEvent);
    }

}
