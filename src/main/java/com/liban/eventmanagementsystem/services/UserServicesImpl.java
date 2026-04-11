package com.liban.eventmanagementsystem.services;

import com.liban.eventmanagementsystem.auth.UserPrincipal;
import com.liban.eventmanagementsystem.model.Privilege;
import com.liban.eventmanagementsystem.model.Role;
import com.liban.eventmanagementsystem.repository.RoleRepository;
import com.liban.eventmanagementsystem.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.liban.eventmanagementsystem.model.User;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServicesImpl implements UserServices {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder enncoder;
    private final RoleRepository roleRepository;

    public UserServicesImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.enncoder = new  BCryptPasswordEncoder(12);
        this.roleRepository = roleRepository;
    }

    @Override
    public User setRoleForUser(UUID user_id, Role role) {
        Optional<User> optionalUser = userRepository.findById(user_id);
        if(optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();
        Role detachedRole = roleRepository.findByRole(role.getRole());
        if(detachedRole != null) {
            user.getRoles().add(detachedRole);
        }
        return userRepository.save(user);
    }

    @Override
    public Set<User> getUsers() {
        return new HashSet<>(userRepository.findAll());
    }

    @Override
    public User getUserById(UUID user_id) {
        return userRepository.findById(user_id).orElse(null);
    }

    @Override
    public User registerUser(User user) {

        if(usernameExists(user.getUsername()) ||  emailExists(user.getEmail())) {
            throw new RuntimeException("Username or Email already exists");
        }

        String encodedPassword = enncoder.encode(user.getPassword());

        Role role = roleRepository.findByRole("USER");

        //user.addRole(role);
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {

        return userRepository.save(user);
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
