package com.fardin.orderservice.services;

import com.shopmate.events.CheckoutEvent;
import com.shopmate.events.InventoryValidationEvent;
import com.shopmate.events.ProductValidationEvent;
import com.shopmate.events.UserValidationEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Component
@Profile("rabbitmq")
public class RabbitMQOrderEventAggregator {

    private static final String REDIS_PREFIX = "order:events:";
    private static final String LOCK_PREFIX = "order:lock:";

    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    OrderService orderService;


    @RabbitListener(queues = "checkout.queue.orderservice")
    public void handleCheckout(CheckoutEvent event) {
        String checkoutId = event.getId();
        String key = REDIS_PREFIX + checkoutId;
        redisTemplate.opsForHash().put(key, "checkout", event);
        redisTemplate.expire(key, Duration.ofSeconds(10));
        tryAggregate(checkoutId, "checkout");
    }

    @RabbitListener(queues = "user-validation.queue.orderservice")
    public void handleUserValidation(UserValidationEvent event) {
        String checkoutId = event.getCheckoutId();
        String key = REDIS_PREFIX + checkoutId;
        redisTemplate.opsForHash().put(key, "user", event);
        redisTemplate.expire(key, Duration.ofSeconds(10));
        tryAggregate(checkoutId, "user");
    }

    @RabbitListener(queues = "product-validation.queue.orderservice")
    public void handleProductValidation(ProductValidationEvent event) {
        String checkoutId = event.getCheckoutId();
        String key = REDIS_PREFIX + checkoutId;
        redisTemplate.opsForHash().put(key, "product", event);
        redisTemplate.expire(key, Duration.ofSeconds(10));
        tryAggregate(checkoutId, "product");
    }

    @RabbitListener(queues = "inventory-validation.queue.orderservice")
    public void onInventoryResponse(InventoryValidationEvent event) {
        String checkoutId = event.getCheckoutId();
        String key = REDIS_PREFIX + checkoutId;
        redisTemplate.opsForHash().put(key, "inventory", event);
        redisTemplate.expire(key, Duration.ofSeconds(10));
        tryAggregate(checkoutId, "inventory");
    }

    private void tryAggregate(String checkoutId, String method) {
        String key = REDIS_PREFIX + checkoutId;
        Map<Object, Object> events = redisTemplate.opsForHash().entries(key);

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
                    events = redisTemplate.opsForHash().entries(key);
                    if (events != null &&
                            events.containsKey("checkout") &&
                            events.containsKey("user") &&
                            events.containsKey("product") &&
                            events.containsKey("inventory")) {

                        redisTemplate.delete(key);

                        CheckoutEvent checkout = (CheckoutEvent) events.get("checkout");
                        UserValidationEvent user = (UserValidationEvent) events.get("user");
                        ProductValidationEvent product = (ProductValidationEvent) events.get("product");
                        InventoryValidationEvent inventory = (InventoryValidationEvent) events.get("inventory");

                        orderService.createOrder(checkout, user, product, inventory);
                    }
                } finally {
                    redisTemplate.delete(lockKey);
                }
            }
        }
    }
}
