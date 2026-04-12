package com.liban.eventmanagementsystem.services;

import com.liban.eventmanagementsystem.auth.UserPrincipal;
import com.liban.eventmanagementsystem.dtos.request.RoleRequestDTO;
import com.liban.eventmanagementsystem.dtos.request.UserRequestDTO;
import com.liban.eventmanagementsystem.dtos.response.UserResponseDTO;
import com.liban.eventmanagementsystem.mapper.UserMapper;
import com.liban.eventmanagementsystem.model.Privilege;
import com.liban.eventmanagementsystem.model.Role;
import com.liban.eventmanagementsystem.repository.RoleRepository;
import com.liban.eventmanagementsystem.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.liban.eventmanagementsystem.model.User;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServicesImpl implements UserServices {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder enncoder;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    public UserServicesImpl(UserRepository userRepository,
                            RoleRepository roleRepository,
                            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.enncoder = new BCryptPasswordEncoder(12);
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponseDTO setRoleForUser(UUID user_id, RoleRequestDTO roleRequestDTO) {
        Optional<User> optionalUser = userRepository.findById(user_id);
        if(optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();
        Role detachedRole = roleRepository.findByRole(roleRequestDTO.getRole());

        if(detachedRole != null) {
            user.getRoles().add(detachedRole);
        }
        return userMapper.toUserResponseDTO(userRepository.save(user));
    }

    @Override
    public Set<UserResponseDTO> getUsers() {

        Set<User> users = new HashSet<>(userRepository.findAll());

        Set<UserResponseDTO> userResponseDTOS = new HashSet<>();

        for(User user : users) {
            userResponseDTOS.add(userMapper.toUserResponseDTO(user));
        }

        return userResponseDTOS;
    }

    @Override
    public UserResponseDTO getUserById(UUID user_id) {
        Optional<User> optionalUser = userRepository.findById(user_id);

        if(optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        return userMapper.toUserResponseDTO(optionalUser.get());
    }

    @Override
    public UserResponseDTO registerUser(UserRequestDTO userRequestDTO) {

        if(usernameExists(userRequestDTO.getUsername()) ||  emailExists(userRequestDTO.getEmail())) {
            throw new RuntimeException("Username or Email already exists");
        }

        User user = userMapper.toUser(userRequestDTO);

        String encodedPassword = enncoder.encode(user.getPassword());

        Role role = roleRepository.findByRole("USER");

        user.getRoles().add(role);
        user.setPassword(encodedPassword);

        return userMapper.toUserResponseDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO updateUser(UUID user_id, UserRequestDTO userRequestDTO) {
        Optional<User> optionalUser = userRepository.findById(user_id);
        if(optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();
        user.setId(user_id);
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setPhoneNumber(userRequestDTO.getPhoneNumber());

        return userMapper.toUserResponseDTO(userRepository.save(user));
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
