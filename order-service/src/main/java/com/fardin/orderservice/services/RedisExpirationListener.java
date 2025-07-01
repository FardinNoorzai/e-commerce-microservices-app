package com.fardin.orderservice.services;

import com.fardin.orderservice.models.Order;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RedisExpirationListener extends KeyExpirationEventMessageListener {

    private final RedisTemplate<String, Object> redisTemplate;
    private final OrderService orderService;

    public RedisExpirationListener(RedisMessageListenerContainer listenerContainer,
                                   RedisTemplate<String, Object> redisTemplate,
                                   OrderService orderService) {
        super(listenerContainer);
        this.redisTemplate = redisTemplate;
        this.orderService = orderService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        if (expiredKey.startsWith("order:events:")) {
            String checkoutId = expiredKey.replace("order:events:", "");
            Order order = orderService.findByCheckoutId(checkoutId);
            if (order == null) {
                orderService.createFailedOrder(checkoutId);
            }
        }

    }
}

