package com.fardin.orderservice.converters;

import com.fardin.orderservice.models.Order;
import com.shopmate.dtos.OrderDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class OrderConvertor implements Converter<Order, OrderDto> {
    @Override
    public OrderDto convert(Order order) {
        return OrderDto.builder()
                .orderId(order.getId())
                .price(order.getPrice())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalAmount())
                .userName(order.getUsername())
                .build();
    }
}
