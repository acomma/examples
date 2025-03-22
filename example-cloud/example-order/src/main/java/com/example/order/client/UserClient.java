package com.example.order.client;

import com.example.common.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "example-user")
public interface UserClient {
    @GetMapping("/user/{userId}")
    User getUser(@PathVariable("userId") Integer userId);
}
