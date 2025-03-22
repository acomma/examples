package com.example.order.controller;

import com.example.common.entity.Order;
import com.example.common.entity.Product;
import com.example.common.entity.User;
import com.example.order.client.ProductClient;
import com.example.order.client.UserClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final UserClient userClient;
    private final ProductClient productClient;

    @Value("${server.port}")
    private int port;

    @GetMapping("/add")
    public Order add(Integer userId, Integer productId, HttpServletRequest request) {
        System.out.println("授权用户信息：" + request.getHeader("X-User"));
        User user = userClient.getUser(userId);
        Product product = productClient.getProduct(productId);

        Order order = new Order();
        order.setUserName(user.getName());
        order.setProductName(product.getName());
        order.setQuantity(port);

        return order;
    }
}
