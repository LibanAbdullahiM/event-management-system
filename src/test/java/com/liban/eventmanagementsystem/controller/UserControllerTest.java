package com.liban.eventmanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.liban.eventmanagementsystem.dtos.request.RoleRequestDTO;
import com.liban.eventmanagementsystem.dtos.request.UserRequestDTO;
import com.liban.eventmanagementsystem.dtos.response.UserResponseDTO;
import com.liban.eventmanagementsystem.exceptions.ResourceNotFoundException;
import com.liban.eventmanagementsystem.model.User;
import com.liban.eventmanagementsystem.services.CustomUserDetailsService;
import com.liban.eventmanagementsystem.services.JWTService;
import com.liban.eventmanagementsystem.services.UserServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureWebMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({TestSecurityConfig.class})
class UserControllerTest {

    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
            "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6ImxpYmFuIiwiaWF0IjoxNTE2MjM5MDIyfQ." +
            "_W_zXJ5VnSE-kP6WIYXIzhSe-Dz2jHwdpfq42AdzeIY";
    private static final UUID USER_ID = UUID.randomUUID();

    @MockitoBean
    private UserServices userServices;

    // Fixes the 'UnsatisfiedDependencyException'
    @MockitoBean private JWTService jwtService;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;

    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private UserRequestDTO requestDTO() {
        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setFirstName("liban");
        requestDTO.setLastName("Abdullahi");
        requestDTO.setPhoneNumber("1234567890");
        requestDTO.setEmail("libanr4243@gmail.com");
        requestDTO.setUsername("liban");
        requestDTO.setPassword("l@12345678");

        return requestDTO;
    }

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @WithAnonymousUser
    void login() throws Exception {
        // 1. Given
        User user = new User();
        user.setUsername("liban");
        user.setPassword("l@123");

        when(userServices.verify(any(User.class))).thenReturn(TOKEN);

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(TOKEN));

        verify(userServices).verify(any(User.class));
    }

    @Test
    @WithAnonymousUser
    void register_ShouldReturn_ValidationError() throws Exception {
        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setUsername("liban");
        requestDTO.setLastName("Abdullahi");

        when(userServices.registerUser(any(UserRequestDTO.class))).thenReturn(new UserResponseDTO());

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Validation Failed"));
    }

    @Test
    @WithAnonymousUser
    void register_ShouldReturn_InvalidEmailFormat() throws Exception {
        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setFirstName("liban");
        requestDTO.setLastName("Abdullahi");
        requestDTO.setPhoneNumber("1234567890");
        requestDTO.setEmail("invalidEmail");
        requestDTO.setUsername("liban");
        requestDTO.setPassword("l@12345678");

        when(userServices.registerUser(any(UserRequestDTO.class))).thenReturn(new UserResponseDTO());

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.errors", hasKey("email")))
                .andExpect(jsonPath("$.errors", aMapWithSize(1)))
                .andExpect(jsonPath("$.errors.email[0]").value("Invalid email format"));
    }

    @Test
    @WithAnonymousUser
    void register_ShouldReturnCreated() throws Exception {

        when(userServices.registerUser(any(UserRequestDTO.class))).thenReturn(new UserResponseDTO());

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isCreated());

        verify(userServices).registerUser(any(UserRequestDTO.class));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void updateUser() throws Exception {

        when(userServices.updateUser(eq(USER_ID), any(UserRequestDTO.class))).thenReturn(new UserResponseDTO());

        mockMvc.perform(put("/auth/{user_id}/edit", USER_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(authenticated())
                .andExpect(status().isOk());

        verify(userServices).updateUser(eq(USER_ID), any(UserRequestDTO.class));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void deleteUser_ShouldReturn_NotFound() throws Exception {

        doThrow(new ResourceNotFoundException("User Not Found"))
                .when(userServices).deleteUser(eq(USER_ID));

        mockMvc.perform(delete("/auth/users/{user_id}/delete", USER_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(authenticated())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("User Not Found"));
    }

    @Test
    @WithMockUser(username = "liban", roles = "USER")
    void deleteUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/auth/users/{user_id}/delete", USER_ID)
                        .with(csrf()))
                .andExpect(authenticated())
                .andExpect(status().isOk());

        verify(userServices).deleteUser(USER_ID);
    }

    @Test
    @WithMockUser(username = "liban", roles = "ADMIN")
    void getAllUsers() throws Exception {
        when(userServices.getUsers()).thenReturn(Set.of(new UserResponseDTO()));

        mockMvc.perform(get("/auth/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userServices).getUsers();
    }

    @Test
    @WithMockUser(username = "liban", roles = "ADMIN")
    void getUserById_ShouldReturn_NotFound() throws Exception {

        when(userServices.getUserById(eq(USER_ID))).thenThrow(new ResourceNotFoundException("User Not Found"));

        mockMvc.perform(get("/auth/users/{user_id}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(authenticated())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("User Not Found"));
    }

    @Test
    @WithMockUser(username = "liban", roles = "ADMIN")
    void getUserById_ShouldReturnOk() throws Exception {

        when(userServices.getUserById(eq(USER_ID))).thenReturn(new UserResponseDTO());

        mockMvc.perform(get("/auth/users/{user_id}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(authenticated())
                .andExpect(status().isOk());

        verify(userServices).getUserById(eq(USER_ID));
    }

    @Test
    @WithMockUser(username = "liban", roles = "ADMIN")
    void setRoleForUser_ShouldReturn_NotFound() throws Exception {
        // Give
        RoleRequestDTO roleRequestDTO = new RoleRequestDTO();
        roleRequestDTO.setRole("ORGANIZER");

        when(userServices.setRoleForUser(eq(USER_ID), any(RoleRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("User Not Found"));

        mockMvc.perform(post("/auth/users/{user_id}/set_role", USER_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleRequestDTO)))
                .andExpect(authenticated())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("User Not Found"));
    }

    @Test
    @WithMockUser(username = "liban", roles = "ADMIN")
    void setRoleForUser_ShouldReturnoK() throws Exception {
        // Give
        RoleRequestDTO roleRequestDTO = new RoleRequestDTO();
        roleRequestDTO.setRole("ORGANIZER");

        when(userServices.setRoleForUser(eq(USER_ID), any(RoleRequestDTO.class)))
                .thenReturn(new UserResponseDTO());

        mockMvc.perform(post("/auth/users/{user_id}/set_role", USER_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleRequestDTO)))
                .andExpect(authenticated())
                .andExpect(status().isOk());

        verify(userServices).setRoleForUser(eq(USER_ID), any(RoleRequestDTO.class));
    }
}