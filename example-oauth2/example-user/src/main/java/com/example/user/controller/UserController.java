package com.example.user.controller;

import com.example.common.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping("/{userId}")
    public User getUser(@PathVariable("userId") Integer id, HttpServletRequest request) {
        System.out.println("授权用户信息：" + request.getHeader("X-User"));
        User user = new User();
        user.setId(id);
        user.setName("user-" + id);
        return user;
    }
}
