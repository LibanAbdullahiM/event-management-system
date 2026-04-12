package com.liban.eventmanagementsystem.services;

import com.liban.eventmanagementsystem.auth.UserPrincipal;
import com.liban.eventmanagementsystem.dtos.request.RoleRequestDTO;
import com.liban.eventmanagementsystem.dtos.request.UserRequestDTO;
import com.liban.eventmanagementsystem.dtos.response.UserResponseDTO;
import com.liban.eventmanagementsystem.model.Role;
import com.liban.eventmanagementsystem.model.User;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Set;
import java.util.UUID;

public interface UserServices {

    UserResponseDTO registerUser(UserRequestDTO userRequestDTO);

    UserResponseDTO updateUser(UUID user_id, UserRequestDTO userRequestDTO);

    void deleteUser(UUID user_id);

    boolean emailExists(String email);

    boolean usernameExists(String username);

    UserResponseDTO setRoleForUser(UUID user_id, RoleRequestDTO roleRequestDTO);

    Set<UserResponseDTO> getUsers();

    UserResponseDTO getUserById(UUID user_id);

}