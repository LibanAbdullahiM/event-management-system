package com.liban.eventmanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.liban.eventmanagementsystem.config.SecurityConfiguration;
import com.liban.eventmanagementsystem.dtos.request.EventRequestDTO;
import com.liban.eventmanagementsystem.dtos.response.EventResponseDTO;
import com.liban.eventmanagementsystem.exceptions.JWTAccessDeniedHandler;
import com.liban.eventmanagementsystem.exceptions.JWTAuthenticationEntryPoint;
import com.liban.eventmanagementsystem.exceptions.ResourceOwnershipException;
import com.liban.eventmanagementsystem.filter.JWTFilter;
import com.liban.eventmanagementsystem.services.CustomUserDetailsService;
import com.liban.eventmanagementsystem.services.EventService;
import com.liban.eventmanagementsystem.services.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@Import(TestSecurityConfig.class)
class EventControllerTest {

    private static final UUID EVENT_ID = UUID.randomUUID();

    @MockitoBean
    private EventService eventService;

    @Autowired private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    // Fixes the 'UnsatisfiedDependencyException'
    @MockitoBean private JWTService jwtService;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;

    private EventRequestDTO requestDTO() {
        EventRequestDTO requestDTO = new EventRequestDTO();
        requestDTO.setTitle("Test Event");
        requestDTO.setDescription("Test Description");
        requestDTO.setStartDate(LocalDate.of(2026, 4, 30));
        requestDTO.setEndDate(LocalDate.of(2026, 5, 6));
        requestDTO.setStartTime(LocalTime.of(15, 30));
        requestDTO.setEndTime(LocalTime.of(19, 30));
        requestDTO.setLocation("Espoo");
        requestDTO.setCapacity(20);

        return requestDTO;
    }

    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper();
        // This allows Jackson to "see" your @JsonFormat annotations on LocalDate
        // solves: InvalidDefinitionException: Java 8 date/time type `java.time.LocalDate` not supported by default
        objectMapper.registerModule(new JavaTimeModule());




        EventResponseDTO event = new EventResponseDTO();
        event.setId(EVENT_ID);
        event.setTitle("Test Event");
        event.setDescription("Test Description");

        given(eventService.getById(EVENT_ID)).willReturn(event);

        given(eventService.getEvents()).willReturn(Set.of(event));
    }

    @Test
    void findAll() throws Exception {

        mockMvc.perform(get("/api/events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(eventService).getEvents();

    }

    @Test
    @WithMockUser(roles = "USER")
    void getEvent() throws Exception {

        mockMvc.perform(get("/api/events/{event_id}", EVENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Event"));

        verify(eventService).getById(EVENT_ID);
    }

    @Test
    @WithMockUser(roles = "ORGANIZER")
    void createEvent_shouldThrowValidationError() throws Exception {
        EventRequestDTO requestDTO = new EventRequestDTO();
        requestDTO.setTitle("Test Event");

        when(eventService.save(any())).thenReturn(new EventResponseDTO());

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Validation Failed"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createEvent_shouldReturnCreated() throws Exception {

        given(eventService.save(any(EventRequestDTO.class))).willReturn(new EventResponseDTO());

        mockMvc.perform(post("/api/events")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(authenticated())
                .andExpect(status().isCreated());

        verify(eventService).save(any(EventRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateEvent_whenUserIsOwner_ShouldReturnUpdatedEvent() throws Exception {
        EventResponseDTO updatedEvent = new EventResponseDTO();
        updatedEvent.setId(EVENT_ID);
        updatedEvent.setTitle("Updated Event");

        given(eventService.update(eq(EVENT_ID), any(EventRequestDTO.class))).willReturn(updatedEvent);

        mockMvc.perform(put("/api/events/{event_id}/edit", EVENT_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Event"));

        verify(eventService).update(eq(EVENT_ID), any(EventRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "ORGANIZER")
    void updateEvent_WhenUserIsNotOwner_ShouldReturnForbidden() throws Exception {

        when(eventService.update(eq(EVENT_ID), any(EventRequestDTO.class)))
                .thenThrow(new ResourceOwnershipException("You are not allowed to updated this"));

        mockMvc.perform(put("/api/events/{event_id}/edit", EVENT_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You are not allowed to updated this"));

        verify(eventService).update(eq(EVENT_ID), any(EventRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteEvent_AsUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/events/{event_id}/delete", EVENT_ID))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteEvent_AsAdmin_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/events/{event_id}/delete", EVENT_ID)
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}