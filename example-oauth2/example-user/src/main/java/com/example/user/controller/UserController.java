package com.example.user.controller;

import com.example.common.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping("/{userId}")
    public User getUser(@PathVariable("userId") Integer id) {
        User user = new User();
        user.setId(id);
        user.setName("user-" + id);
        return user;
    }
}
