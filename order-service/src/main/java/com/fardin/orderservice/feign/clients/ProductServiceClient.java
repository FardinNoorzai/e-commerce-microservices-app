package com.fardin.orderservice.feign.clients;

import com.shopmate.events.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductServiceClient {
    @GetMapping("api/products/{productId}")
    public ProductDto getProduct(@PathVariable("productId") String productId);
}
