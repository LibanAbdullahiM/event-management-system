package com.liban.eventmanagementsystem.controller;

import com.liban.eventmanagementsystem.model.User;
import com.liban.eventmanagementsystem.services.UserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserServices userServices;

    @PostMapping("/auth/login")
    public String login(@RequestBody User user) {

        return "Success with the username: " + user.getUsername() + " and password: " + user.getPassword();
    }

    @PostMapping("/auth/register")
    public User register(@RequestBody User user) {

        return userServices.registerUser(user);
    }

    @PutMapping("/auth/{user_id}/edit")
    public User updateUser(@PathVariable UUID  user_id, @RequestBody User user) {
        return userServices.updateUser(user_id, user);
    }

    @DeleteMapping("/auth/{user_id}/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable UUID  user_id) {
        userServices.deleteUser(user_id);
    }
}
