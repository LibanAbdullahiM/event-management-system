package com.liban.eventmanagementsystem.controller;

import com.liban.eventmanagementsystem.model.Role;
import com.liban.eventmanagementsystem.model.User;
import com.liban.eventmanagementsystem.services.UserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final UserServices userServices;

    @PostMapping("/login")
    public String login(@RequestBody User user) {

        return "Success with the username: " + user.getUsername() + " and password: " + user.getPassword();
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {

        return userServices.registerUser(user);
    }

    @PutMapping("/{user_id}/edit")
    public User updateUser(@PathVariable UUID  user_id, @RequestBody User user) {
        user.setId(user_id);
        return userServices.updateUser(user);
    }

    @DeleteMapping("/{user_id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable UUID  user_id) {
        userServices.deleteUser(user_id);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Set<User> getAllUsers() {
        return userServices.getUsers();
    }

    @GetMapping("/{user_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public User getUserById(@PathVariable UUID user_id) {

        return userServices.getUserById(user_id);
    }

    @PostMapping("/{user_id}/set_role")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public User setRoleForUser(@PathVariable UUID  user_id,
                               @RequestBody Role role) {
        return userServices.setRoleForUser(user_id, role);
    }
}
