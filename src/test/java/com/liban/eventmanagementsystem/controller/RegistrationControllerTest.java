package com.liban.eventmanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.liban.eventmanagementsystem.dtos.request.RegistrationRequestDTO;
import com.liban.eventmanagementsystem.dtos.response.RegistrationResponseDTO;
import com.liban.eventmanagementsystem.exceptions.ResourceNotFoundException;
import com.liban.eventmanagementsystem.services.CustomUserDetailsService;
import com.liban.eventmanagementsystem.services.JWTService;
import com.liban.eventmanagementsystem.services.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest {

    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID REGISTRATION_ID = UUID.randomUUID();


    // Fixes the 'UnsatisfiedDependencyException'
    @MockitoBean private JWTService jwtService;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;

    @MockitoBean private RegistrationService registrationService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getRegistrationsByUser() throws Exception {

        when(registrationService.findByUser(USER_ID)).thenReturn(Set.of(new RegistrationResponseDTO()));

        mockMvc.perform(get("/api/{user_id}/registrations", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(authenticated())
                .andExpect(status().isOk());

        verify(registrationService).findByUser(any());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void createRegistration_ShouldReturnValidationError() throws Exception {
        RegistrationRequestDTO requestDTO = new RegistrationRequestDTO();

        when(registrationService.createRegistration(any(RegistrationRequestDTO.class))).thenReturn(new RegistrationResponseDTO());

        mockMvc.perform(post("/api/{user_id}/registrations/{event_id}/register", USER_ID, EVENT_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(authenticated())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Validation Failed"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void createRegistration_ShouldReturnInvalidEmailFormat() throws Exception {
        // 1. Given
        RegistrationRequestDTO requestDTO = new RegistrationRequestDTO();
        requestDTO.setFirstName("Liban");
        requestDTO.setLastName("Abdullahi");
        requestDTO.setEmail("email");
        requestDTO.setPhoneNumber("1234567890");

        when(registrationService.createRegistration(any(RegistrationRequestDTO.class))).thenReturn(new RegistrationResponseDTO());

        // 2. when
        mockMvc.perform(post("/api/{user_id}/registrations/{event_id}/register", USER_ID, EVENT_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(authenticated())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.errors", aMapWithSize(1)))
                .andExpect(jsonPath("$.errors", hasKey("email")))
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.email[0]").value("Invalid email format"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void createRegistration_ShouldReturnCreated() throws Exception {
        // 1. Given
        RegistrationRequestDTO requestDTO = new RegistrationRequestDTO();
        requestDTO.setFirstName("Liban");
        requestDTO.setLastName("Abdullahi");
        requestDTO.setEmail("libanr4243@gmail.com");
        requestDTO.setPhoneNumber("1234567890");

        // captor the value passed to verify internal setters
        ArgumentCaptor<RegistrationRequestDTO> captor = ArgumentCaptor.forClass(RegistrationRequestDTO.class);
        when(registrationService.createRegistration(captor.capture())).thenReturn(new RegistrationResponseDTO());

        // 2. when
        mockMvc.perform(post("/api/{user_id}/registrations/{event_id}/register", USER_ID, EVENT_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(authenticated())
                .andExpect(status().isCreated());

        RegistrationRequestDTO savedDto = captor.getValue();
        assertEquals(USER_ID, savedDto.getUser_id());
        assertEquals(EVENT_ID, savedDto.getEvent_id());
        verify(registrationService).createRegistration(any(RegistrationRequestDTO.class));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void cancelRegistration_ShouldReturnNotFound() throws  Exception {

        // Mock service to throw exception
        doThrow(new ResourceNotFoundException("Registration Not Found!"))
                .when(registrationService).cancelRegistration(REGISTRATION_ID);

        mockMvc.perform(delete("/api/{user_id}/registrations/{registration_id}/delete", USER_ID, REGISTRATION_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(authenticated())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Registration Not Found!"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void cancelRegistration_ShouldReturnOk() throws Exception {

        mockMvc.perform(delete("/api/{user_id}/registrations/{registration_id}/delete", USER_ID, REGISTRATION_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }
}