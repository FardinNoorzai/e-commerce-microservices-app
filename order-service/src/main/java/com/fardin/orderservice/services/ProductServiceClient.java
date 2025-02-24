package com.fardin.orderservice.services;

import com.shopmate.dtos.ProductDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @GetMapping("api/products/{productId}")
    public ProductDto getProduct(@PathVariable("productId") String productId);

}
