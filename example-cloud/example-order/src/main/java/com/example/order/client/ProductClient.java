package com.example.order.client;

import com.example.common.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "example-product")
public interface ProductClient {
    @GetMapping("/product/{productId}")
    Product getProduct(@PathVariable("productId") Integer productId);
}
