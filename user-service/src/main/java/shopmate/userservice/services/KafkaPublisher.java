package shopmate.userservice.services;

import com.shopmate.events.CheckoutEvent;
import com.shopmate.events.UserValidationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import shopmate.userservice.models.User;

import java.time.Instant;

@Service
@Profile("kafka")
public class KafkaPublisher implements UserValidationPublisher {

    @Autowired
    CustomUserDetailsService customUserDetailsService;
    @Autowired
    KafkaTemplate<String, UserValidationEvent> kafkaTemplate;

    @Override
    public void publish(CheckoutEvent dto) {
        User user = customUserDetailsService.findByUsername(dto.getUsername());
        UserValidationEvent userValidationEvent = new UserValidationEvent();
        if(user != null){
            if(user.isEnabled() && user.isAccountNonExpired() && user.isAccountNonLocked() && user.isCredentialsNonExpired()) {
                userValidationEvent.setCheckoutId(dto.getId());
                userValidationEvent.setUsername(dto.getUsername());
                userValidationEvent.setValid(true);
            }
        }else{
            userValidationEvent.setCheckoutId(dto.getId());
            userValidationEvent.setUsername(dto.getUsername());
            userValidationEvent.setValid(false);
        }
        sendUserValidationEvent("user-validation", userValidationEvent);
    }

    public void sendUserValidationEvent(String topic,UserValidationEvent userValidationEvent) {
        userValidationEvent.setTimestamp(Instant.now().toEpochMilli());
        kafkaTemplate.send(topic, userValidationEvent);
    }
}
