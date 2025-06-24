package shopmate.userservice.kafka;

import com.shopmate.events.CheckoutEvent;
import com.shopmate.events.UserValidationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import shopmate.userservice.models.User;
import shopmate.userservice.services.CustomUserDetailsService;

@Service
public class CheckoutListener {

    @Autowired
    CustomUserDetailsService  customUserDetailsService;

    @Autowired
    KafkaPublisher  kafkaPublisher;

    @KafkaListener(topics = "checkout",groupId = "user-service-group")
    public void listen(CheckoutEvent dto) {
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
        kafkaPublisher.sendUserValidationEvent("user-validation", userValidationEvent);
    }
}
