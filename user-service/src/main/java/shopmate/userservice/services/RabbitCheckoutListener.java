package shopmate.userservice.services;


import com.shopmate.events.CheckoutEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("rabbitmq")
public class RabbitCheckoutListener {

    @Autowired
    private RabbitPublisher rabbitPublisher;

    @RabbitListener(queues = "checkout.queue.userservice")
    public void listen(CheckoutEvent dto) {
        System.out.println("📥 Received CheckoutEvent from RabbitMQ: " + dto.getUsername());
        rabbitPublisher.publish(dto);
    }
}

