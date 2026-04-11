package com.liban.eventmanagementsystem.services;

import com.liban.eventmanagementsystem.auth.UserPrincipal;
import com.liban.eventmanagementsystem.model.Role;
import com.liban.eventmanagementsystem.model.User;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Set;
import java.util.UUID;

public interface UserServices {

    User registerUser(User user);

    User updateUser(User user);

    void deleteUser(UUID user_id);

    boolean emailExists(String email);

    boolean usernameExists(String username);

    User setRoleForUser(UUID user_id, Role role);

    Set<User> getUsers();

    User getUserById(UUID user_id);

}