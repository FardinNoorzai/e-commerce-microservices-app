package com.fardin.orderservice.services;

import com.shopmate.events.CheckoutEvent;
import com.shopmate.events.InventoryValidationEvent;
import com.shopmate.events.ProductValidationEvent;
import com.shopmate.events.UserValidationEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Component
@Profile("kafka")
public class KafkaOrderEventAggregator {

    private static final String REDIS_PREFIX = "order:events:";
    private static final String LOCK_PREFIX = "order:lock:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final OrderService orderService;

    public KafkaOrderEventAggregator(RedisTemplate<String, Object> redisTemplate,
                                     OrderService orderService) {
        this.redisTemplate = redisTemplate;
        this.orderService = orderService;
    }

    @KafkaListener(topics = "checkout", groupId = "order-service-group")
    public void handleCheckout(CheckoutEvent event) {
        String checkoutId = event.getId();
        String key = REDIS_PREFIX + checkoutId;
        redisTemplate.opsForHash().put(key, "checkout", event);
        redisTemplate.expire(key, Duration.ofSeconds(10));
        tryAggregate(checkoutId, "checkout"); // <== added
    }

    @KafkaListener(topics = "user-validation", groupId = "order-service-group")
    public void handleUserValidation(UserValidationEvent event) {
        String checkoutId = event.getCheckoutId();
        String key = REDIS_PREFIX + checkoutId;
        redisTemplate.opsForHash().put(key, "user", event);
        redisTemplate.expire(key, Duration.ofSeconds(10));
        tryAggregate(checkoutId, "user"); // <== added
    }

    @KafkaListener(topics = "product-validation", groupId = "order-service-group")
    public void handleProductValidation(ProductValidationEvent event) {
        String checkoutId = event.getCheckoutId();
        String key = REDIS_PREFIX + checkoutId;
        redisTemplate.opsForHash().put(key, "product", event);
        redisTemplate.expire(key, Duration.ofSeconds(10));
        tryAggregate(checkoutId, "product"); // <== added
    }

    @KafkaListener(topics = "inventory-validation", groupId = "order-service-group")
    public void onInventoryResponse(InventoryValidationEvent event) {
        String checkoutId = event.getCheckoutId();
        String key = REDIS_PREFIX + checkoutId;
        redisTemplate.opsForHash().put(key, "inventory", event);
        redisTemplate.expire(key, Duration.ofSeconds(10));
        tryAggregate(checkoutId, "inventory"); // <== added
    }


    private void tryAggregate(String checkoutId, String method) {
        String key = REDIS_PREFIX + checkoutId;
        Map<Object, Object> events = redisTemplate.opsForHash().entries(key);

        // Check if all required events are present
        if (events != null &&
                events.containsKey("checkout") &&
                events.containsKey("user") &&
                events.containsKey("product") &&
                events.containsKey("inventory")) {

            String lockKey = LOCK_PREFIX + checkoutId;
            Boolean locked = redisTemplate.opsForValue().setIfAbsent(
                    lockKey, "locked", Duration.ofSeconds(5)
            );

            if (Boolean.TRUE.equals(locked)) {
                try {
                    // Re-fetch events after acquiring lock to handle expiry/processing
                    events = redisTemplate.opsForHash().entries(key);
                    if (events != null &&
                            events.containsKey("checkout") &&
                            events.containsKey("user") &&
                            events.containsKey("product") &&
                            events.containsKey("inventory")) {

                        // Delete events BEFORE processing to prevent duplicates
                        redisTemplate.delete(key);

                        CheckoutEvent checkout = (CheckoutEvent) events.get("checkout");
                        UserValidationEvent user = (UserValidationEvent) events.get("user");
                        ProductValidationEvent product = (ProductValidationEvent) events.get("product");
                        InventoryValidationEvent inventory = (InventoryValidationEvent) events.get("inventory");

                        orderService.createOrder(checkout, user, product,inventory);
                    }
                } finally {
                    // Always release the lock
                    redisTemplate.delete(lockKey);
                }
            }
        }
    }

}
