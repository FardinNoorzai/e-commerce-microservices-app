package shopmate.productservice.services;

import com.shopmate.events.CartItem;
import com.shopmate.events.CheckoutEvent;
import com.shopmate.events.ProductValidationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import shopmate.productservice.models.Product;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Profile("kafka")
public class KafkaCheckoutListener {

    @Autowired
    ProductService productService;

    @Autowired
    KafkaProductValidationPublisher kafkaPublisher;
    @KafkaListener(topics = "checkout",groupId = "product-service-group")
    public void onCheckout(CheckoutEvent checkoutEvent) {
        for (CartItem cartItem : checkoutEvent.getCartItems()) {
            Optional<Product> optionalProduct = productService.findById(cartItem.getProductId().intValue());

            boolean isInvalid = optionalProduct
                    .map(product -> {
                        System.out.println(product.getPrice().toString() + " : " + cartItem.getPrice());
                        return !product.getPrice().equals(new BigDecimal(cartItem.getPrice()));
                    })
                    .orElse(true);

            if (isInvalid) {
                publishValidationEvent(cartItem.getProductId(), checkoutEvent.getId(), false);
                return;
            }
        }
        publishValidationEvent(null, checkoutEvent.getId(), true);
    }

    private void publishValidationEvent(Long productId, String checkoutId, boolean isValid) {
        ProductValidationEvent event = new ProductValidationEvent();
        event.setProductId(productId);
        event.setCheckoutId(checkoutId);
        event.setValid(isValid);
        kafkaPublisher.publish(event);
    }
}
