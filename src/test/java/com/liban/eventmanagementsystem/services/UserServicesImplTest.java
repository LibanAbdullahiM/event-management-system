package com.liban.eventmanagementsystem.services;

import com.liban.eventmanagementsystem.dtos.request.RoleRequestDTO;
import com.liban.eventmanagementsystem.dtos.request.UserRequestDTO;
import com.liban.eventmanagementsystem.dtos.response.UserResponseDTO;
import com.liban.eventmanagementsystem.exceptions.ResourceNotFoundException;
import com.liban.eventmanagementsystem.mapper.UserMapper;
import com.liban.eventmanagementsystem.mapper.UserMapperImpl;
import com.liban.eventmanagementsystem.model.Role;
import com.liban.eventmanagementsystem.model.User;
import com.liban.eventmanagementsystem.repository.RoleRepository;
import com.liban.eventmanagementsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServicesImplTest {

    private UserServicesImpl userServices;

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;

    private final BCryptPasswordEncoder encoder;
    private final UserMapper mapper;
    private final JWTService jwtService;

    @Mock private AuthenticationManager authenticationManager;
    @Mock private Authentication authentication;

    public UserServicesImplTest() {
        this.encoder = new BCryptPasswordEncoder(12);
        this.mapper = new UserMapperImpl();
        this.jwtService = new JWTService();
    }

    @BeforeEach
    void setUp() {
        userServices = new UserServicesImpl(userRepository, roleRepository, mapper, authenticationManager, jwtService);
    }

    @Test
    void verifyUser() {
        // 1. Given
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("username");
        user.setPassword("password");

        // mocks
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        // 2. When
        String token = userServices.verify(user);
        String username = jwtService.extractUsername(token);

        // 3. Then
        assertNotNull(token);
        assertEquals(username, user.getUsername());
    }

    @Test
    void getUsers() {
        // 1. Given
        User user1 = new User();
        user1.setId(UUID.randomUUID());

        User user2 = new User();
        user2.setId(UUID.randomUUID());

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // 2. When
        Set<UserResponseDTO> responseDTOSet = userServices.getUsers();

        // 3. Then
        assertEquals(2, responseDTOSet.size());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById() {
        // 1. Given
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("Test");
        user.setPassword("Test");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // 2. When
        UserResponseDTO responseDTO = userServices.getUserById(userId);

        // 3. Then
        assertEquals("Test", responseDTO.getUsername());
        verify(userRepository).findById(any());

    }

    @Test
    void registerUser() {
        // 1. Given
        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setUsername("Test");
        requestDTO.setPassword("Test");

        Role role = new Role();
        role.setRole("USER");

        when(userRepository.findByEmail(any())).thenReturn(null);
        when(userRepository.findByUsername(any())).thenReturn(null);
        when(roleRepository.findByRole(any())).thenReturn(role);

        //Capture the User to verify the internal setters
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenReturn(new  User());

        // 2. When
        userServices.registerUser(requestDTO);

        // 3. Then
        User savedUser = userCaptor.getValue();

        assertEquals("USER", savedUser.getRoles().iterator().next().getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser() {
        // 1. Given
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setUsername("new username");
        requestDTO.setPassword("new password");

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // Capture the User to verify the internal setters
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenReturn(user);

        // 2. When
        userServices.updateUser(userId, requestDTO);

        // 3. Then
        User savedUser = userCaptor.getValue();
        assertEquals(userId, savedUser.getId());
        assertEquals(requestDTO.getUsername(), savedUser.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void setRoleForUser() {
        // 1. Given
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        Role role = new Role();
        role.setRole("ORGANIZER");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByRole(any())).thenReturn(role);

        // Capture the User object passed to the repository to verify internal sesters
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenReturn(user);

        // 2. When
        userServices.setRoleForUser(userId, new RoleRequestDTO());

        // 3. Then
        User savedUser = userCaptor.getValue();
        assertEquals(role.getRole(), savedUser.getRoles().iterator().next().getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser() {
        // 1. Given
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // 2. When
        userServices.deleteUser(userId);

        // 3. Then
        verify(userRepository).delete(any(User.class));
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> userServices.deleteUser(UUID.randomUUID()));
    }
}