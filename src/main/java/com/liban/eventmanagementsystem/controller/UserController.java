package com.liban.eventmanagementsystem.controller;

import com.liban.eventmanagementsystem.dtos.request.RoleRequestDTO;
import com.liban.eventmanagementsystem.dtos.request.UserRequestDTO;
import com.liban.eventmanagementsystem.dtos.response.UserResponseDTO;
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
    public UserResponseDTO register(@RequestBody UserRequestDTO userRequestDTO) {

        return userServices.registerUser(userRequestDTO);
    }

    @PutMapping("/{user_id}/edit")
    public UserResponseDTO updateUser(@PathVariable UUID  user_id, @RequestBody UserRequestDTO userRequestDTO) {

        return userServices.updateUser(user_id, userRequestDTO);
    }

    @DeleteMapping("/{user_id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable UUID  user_id) {
        userServices.deleteUser(user_id);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Set<UserResponseDTO> getAllUsers() {
        return userServices.getUsers();
    }

    @GetMapping("/{user_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDTO getUserById(@PathVariable UUID user_id) {

        return userServices.getUserById(user_id);
    }

    @PostMapping("/{user_id}/set_role")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO setRoleForUser(@PathVariable UUID  user_id,
                               @RequestBody RoleRequestDTO roleRequestDTO) {
        return userServices.setRoleForUser(user_id, roleRequestDTO);
    }
}
