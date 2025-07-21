package shopmate.userservice.services;

import com.shopmate.events.CheckoutEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Profile("kafka")
public class KafkaCheckoutListener {


    @Autowired
    KafkaPublisher  kafkaPublisher;

    @KafkaListener(topics = "checkout",groupId = "user-service-group")
    public void listen(CheckoutEvent dto) {
        kafkaPublisher.publish(dto);
    }

}
