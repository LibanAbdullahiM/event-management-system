package com.liban.eventmanagementsystem.controller;

import com.liban.eventmanagementsystem.dtos.request.RoleRequestDTO;
import com.liban.eventmanagementsystem.dtos.request.UserRequestDTO;
import com.liban.eventmanagementsystem.dtos.response.UserResponseDTO;
import com.liban.eventmanagementsystem.model.Role;
import com.liban.eventmanagementsystem.model.User;
import com.liban.eventmanagementsystem.services.UserServices;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final UserServices userServices;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User user) {

        String generatedToken = userServices.verify(user);

        return Map.of("token", generatedToken);
    }

    @Operation(summary = "create a new account", description = "Allows users to create a new account.")
    @PostMapping("/register")
    public UserResponseDTO register(@Valid @RequestBody UserRequestDTO userRequestDTO) {

        return userServices.registerUser(userRequestDTO);
    }

    @Operation(summary = "Edit profile/user details", description = "Allows users to update their details if they want to.")
    @PutMapping("/{user_id}/edit")
    public UserResponseDTO updateUser(@PathVariable UUID  user_id,
                                      @Valid @RequestBody UserRequestDTO userRequestDTO) {

        return userServices.updateUser(user_id, userRequestDTO);
    }

    @Operation(summary = "Delete an account", description = "Allows the users to delete their account.")
    @DeleteMapping("/{user_id}/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable UUID  user_id) {
        userServices.deleteUser(user_id);
    }

    @Operation(summary = "Get All users", description = "Allows Admins to get list of registered users.")
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Set<UserResponseDTO> getAllUsers() {
        return userServices.getUsers();
    }

    @Operation(summary = "Get user by ID", description = "Allows Admins to get user by ID.")
    @GetMapping("/{user_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDTO getUserById(@PathVariable UUID user_id) {

        return userServices.getUserById(user_id);
    }

    @Operation(summary = "Set role to user", description = "Allows Admins to give users a role.")
    @PostMapping("/{user_id}/set_role")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO setRoleForUser(@PathVariable UUID  user_id,
                               @RequestBody RoleRequestDTO roleRequestDTO) {
        return userServices.setRoleForUser(user_id, roleRequestDTO);
    }
}
