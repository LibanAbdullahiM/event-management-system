package com.liban.eventmanagementsystem.services;

import com.liban.eventmanagementsystem.dtos.request.RoleRequestDTO;
import com.liban.eventmanagementsystem.dtos.request.UserRequestDTO;
import com.liban.eventmanagementsystem.dtos.response.UserResponseDTO;
import com.liban.eventmanagementsystem.exceptions.ResourceAlreadyExistsException;
import com.liban.eventmanagementsystem.exceptions.ResourceNotFoundException;
import com.liban.eventmanagementsystem.mapper.UserMapper;
import com.liban.eventmanagementsystem.model.Role;
import com.liban.eventmanagementsystem.repository.RoleRepository;
import com.liban.eventmanagementsystem.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.liban.eventmanagementsystem.model.User;

import java.util.*;

@Service
public class UserServicesImpl implements UserServices {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authManager;
    private final JWTService jwtService;

    public UserServicesImpl(UserRepository userRepository,
                            RoleRepository roleRepository,
                            UserMapper userMapper,
                            AuthenticationManager authManager,
                            JWTService jwtService) {
        this.userRepository = userRepository;
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.encoder = new BCryptPasswordEncoder(12);
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    @Override
    public String verify(User user) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()));

        return jwtService.generateToken(user.getUsername());

    }

    @Override
    public UserResponseDTO setRoleForUser(UUID user_id, RoleRequestDTO roleRequestDTO) {
        Optional<User> optionalUser = userRepository.findById(user_id);
        if(optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
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
            throw new ResourceNotFoundException("User not found");
        }

        return userMapper.toUserResponseDTO(optionalUser.get());
    }

    @Override
    public UserResponseDTO registerUser(UserRequestDTO userRequestDTO) {

        if(usernameExists(userRequestDTO.getUsername())) {
            throw new ResourceAlreadyExistsException("Username already exists");
        }

        if (emailExists(userRequestDTO.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        User user = userMapper.toUser(userRequestDTO);

        String encodedPassword = encoder.encode(user.getPassword());

        Role role = roleRepository.findByRole("USER");

        user.getRoles().add(role);
        user.setPassword(encodedPassword);

        return userMapper.toUserResponseDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO updateUser(UUID user_id, UserRequestDTO userRequestDTO) {
        Optional<User> optionalUser = userRepository.findById(user_id);
        if(optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        User user = optionalUser.get();
        user.setId(user_id);
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setPhoneNumber(userRequestDTO.getPhoneNumber());

        //TODO UPDATE THE PASSWORD SEPARATELY.
        user.setPassword(encoder.encode(userRequestDTO.getPassword()));

        return userMapper.toUserResponseDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(UUID user_id) {
        Optional<User> userOptional = userRepository.findById(user_id);
        if(userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
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
