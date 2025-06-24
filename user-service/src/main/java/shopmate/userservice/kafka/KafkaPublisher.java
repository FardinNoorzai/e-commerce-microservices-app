package shopmate.userservice.kafka;

import com.shopmate.events.UserValidationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaPublisher {

    @Autowired
    KafkaTemplate<String, UserValidationEvent> kafkaTemplate;
    public void sendUserValidationEvent(String topic,UserValidationEvent userValidationEvent) {
        kafkaTemplate.send(topic, userValidationEvent);
    }

}
