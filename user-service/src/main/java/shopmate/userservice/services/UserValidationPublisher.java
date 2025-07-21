package shopmate.userservice.services;

import com.shopmate.events.CheckoutEvent;

public interface UserValidationPublisher {
    public void publish(CheckoutEvent checkoutEvent);
}
