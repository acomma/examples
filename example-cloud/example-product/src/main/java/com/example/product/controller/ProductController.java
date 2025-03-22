package com.example.product.controller;

import com.example.common.entity.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Value("${server.port}")
    private int port;

    @GetMapping("/{productId}")
    public Product getProduct(@PathVariable("productId") Integer id) {
        Product product = new Product();
        product.setId(id);
        product.setName("product-" + id + "-" + port);
        return product;
    }
}
