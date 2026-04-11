package com.liban.eventmanagementsystem.services;

import com.liban.eventmanagementsystem.model.Privilege;
import com.liban.eventmanagementsystem.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.liban.eventmanagementsystem.model.User;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServicesImpl implements UserServices {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder enncoder;

    public UserServicesImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.enncoder = new  BCryptPasswordEncoder(12);
    }

    @Override
    public User registerUser(User user) {

        if(usernameExists(user.getUsername()) ||  emailExists(user.getEmail())) {
            throw new RuntimeException("Username or Email already exists");
        }

        String encodedPassword = enncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    @Override
    public User updateUser(UUID user_id, User user) {
        Optional<User> userOptional = userRepository.findById(user_id);

        if(userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User detachedUser = userOptional.get();

        detachedUser.setId(user_id);
        detachedUser.setUsername(user.getUsername());
        detachedUser.setEmail(user.getEmail());
        detachedUser.setFirstName(user.getFirstName());
        detachedUser.setLastName(user.getLastName());
        detachedUser.setPhoneNumber(user.getPhoneNumber());

        return userRepository.save(detachedUser);
    }

    @Override
    public void deleteUser(UUID user_id) {
        Optional<User> userOptional = userRepository.findById(user_id);
        if(userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User detachedUser = userOptional.get();
        userRepository.delete(detachedUser);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username) != null;
    }
}
