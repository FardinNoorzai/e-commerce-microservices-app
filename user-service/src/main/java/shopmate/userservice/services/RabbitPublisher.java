package shopmate.userservice.services;

import com.shopmate.events.CheckoutEvent;
import com.shopmate.events.UserValidationEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import shopmate.userservice.models.User;

@Service
@Profile("rabbitmq")
public class RabbitPublisher implements UserValidationPublisher {

    private static final String EXCHANGE_NAME = "user-validation.exchange";
    private static final String ROUTING_KEY = "user-validation";

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void publish(CheckoutEvent dto) {
        User user = customUserDetailsService.findByUsername(dto.getUsername());
        UserValidationEvent userValidationEvent = new UserValidationEvent();

        if (user != null &&
                user.isEnabled() &&
                user.isAccountNonExpired() &&
                user.isAccountNonLocked() &&
                user.isCredentialsNonExpired()) {
            userValidationEvent.setValid(true);
        } else {
            userValidationEvent.setValid(false);
        }

        userValidationEvent.setCheckoutId(dto.getId());
        userValidationEvent.setUsername(dto.getUsername());

        sendUserValidationEvent(userValidationEvent);
    }

    public void sendUserValidationEvent(UserValidationEvent event) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, event);
        System.out.println("✅ Sent UserValidationEvent to RabbitMQ");
    }
}

