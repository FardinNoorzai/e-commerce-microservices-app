package com.fardin.orderservice.services;

import com.shopmate.events.PaymentEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("rabbitmq")
public class RabbitPaymentListener {

    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = "payments.queue.orderservice")
    public void listen(PaymentEvent event) throws Exception {
        System.out.println("Received Payment Event " + event);
        orderService.updateOrderWithPaymentInformation(event);
    }
}
