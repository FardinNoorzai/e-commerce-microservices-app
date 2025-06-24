package shopmate.productservice.kafka;

import com.shopmate.events.ProductValidationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaPublisher {

    @Autowired
    KafkaTemplate<String, ProductValidationEvent> kafkaTemplate;

    public void publish(ProductValidationEvent productValidationEvent) {
        kafkaTemplate.send("product-validation", productValidationEvent);
    }

}
