package com.liban.eventmanagementsystem.controller;

import com.liban.eventmanagementsystem.model.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @PostMapping("/login")
    public String login(@RequestBody User user) {

        return "Success with the username: " + user.getUsername() + " and password: " + user.getPassword();
    }
}
