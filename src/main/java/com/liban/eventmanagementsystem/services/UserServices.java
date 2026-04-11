package com.liban.eventmanagementsystem.services;

import com.liban.eventmanagementsystem.model.User;

import java.util.UUID;

public interface UserServices {

    User registerUser(User user);

    User updateUser(UUID user_id, User user);

    void deleteUser(UUID user_id);

    boolean emailExists(String email);
    boolean usernameExists(String username);

}
