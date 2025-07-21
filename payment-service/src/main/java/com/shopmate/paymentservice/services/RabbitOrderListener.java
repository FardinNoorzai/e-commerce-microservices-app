package com.shopmate.paymentservice.services;


import com.shopmate.events.OrderEvent;
import com.shopmate.events.PaymentEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("rabbitmq")
public class RabbitOrderListener {

    private static final String EXCHANGE = "payments.exchange";
    private static final String ROUTING_KEY = "payments";

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "orders.queue.paymentservice")
    public void listen(OrderEvent orderEvent) throws Exception {
        PaymentEvent event = checkoutService.createCheckoutSession(orderEvent);
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, event);
    }
}

