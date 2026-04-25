package com.liban.eventmanagementsystem.services;

import com.liban.eventmanagementsystem.auth.UserPrincipal;
import com.liban.eventmanagementsystem.dtos.request.EventRequestDTO;
import com.liban.eventmanagementsystem.dtos.response.EventResponseDTO;
import com.liban.eventmanagementsystem.exceptions.ResourceOwnershipException;
import com.liban.eventmanagementsystem.mapper.EventMapper;
import com.liban.eventmanagementsystem.mapper.EventMapperImpl;
import com.liban.eventmanagementsystem.model.Event;
import com.liban.eventmanagementsystem.model.User;
import com.liban.eventmanagementsystem.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock private EventRepository eventRepository;
    private final EventMapper eventMapper;

    private EventServiceImpl eventService;

    // Mocking Security Context
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    EventServiceImplTest() {
        this.eventMapper = new EventMapperImpl();
    }

    @BeforeEach
    void setUp() {
        // set the security context
        SecurityContextHolder.setContext(securityContext);

        eventService = new EventServiceImpl(eventRepository, eventMapper);
    }

    @Test
    void getEvents() {
        // 1. Given
        Event ev1 = new Event();
        ev1.setTitle("Event 1");
        ev1.setDescription("Event 1");

        Event ev2 = new Event();
        ev2.setTitle("Event 2");
        ev2.setDescription("Event 2");

        when(eventRepository.findAll()).thenReturn(List.of(ev1, ev2));

        // 2. When
        Set<EventResponseDTO> eventResponseDTOS = eventService.getEvents();

        // 3. Then
        assertEquals(2, eventResponseDTOS.size());
        verify(eventRepository).findAll();
    }

    @Test
    void getById() {
        // 1. Given
        UUID eventId = UUID.randomUUID();
        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Title");
        event.setDescription("Test description");

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // 2. when
        EventResponseDTO responseDTO = eventService.getById(eventId);

        // 3. then
        assertEquals(eventId, responseDTO.getId());
        assertEquals("Title", responseDTO.getTitle());
        verify(eventRepository).findById(any());
    }

    @Test
    void save_ShouldReturnSavedEventResponse() {
        // 1. Given: Request DTO with test data
        EventRequestDTO requestDTO = new EventRequestDTO();
        requestDTO.setTitle("Hackathon");
        requestDTO.setDescription("Coding challange");

        User user = new User();
        user.setUsername("liban");
        user.setPassword("password");

        UserPrincipal userPrincipal = new UserPrincipal(user);

        // SetUp Mocks
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        // Capture the Event object passed to the repository to verify internal sesters
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);

        Event event = eventMapper.toEvent(requestDTO);

        when(eventRepository.save(eventCaptor.capture())).thenReturn(event);

        // 2. when
        EventResponseDTO responseDTO = eventService.save(requestDTO);


        // 3. then
        assertEquals(requestDTO.getTitle(), responseDTO.getTitle());
        assertEquals(requestDTO.getDescription(), responseDTO.getDescription());

        //Verify the 'save' method was called with the correct object and status
        Event captoredEvent = eventCaptor.getValue();
        assertEquals("Open", captoredEvent.getStatus());
        assertEquals(user, captoredEvent.getCreatedBy());
        verify(eventRepository).save(any(Event.class));

    }

    @Test
    void update_ShouldUpdateFieldsCorrectly_WhenUserIsOwner() {
        // 1. Given
        UUID ownerId = UUID.randomUUID();
        User owner = new User();
        owner.setId(ownerId);

        UUID eventId = UUID.randomUUID();
        Event existingEvent = new Event();
        existingEvent.setId(eventId);
        existingEvent.setCreatedBy(owner);

        EventRequestDTO requestDTO = new EventRequestDTO();
        requestDTO.setTitle("New Title");
        requestDTO.setCapacity(30);
        requestDTO.setDescription("New Description");

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new UserPrincipal(owner));
        when(eventRepository.save(any(Event.class))).thenReturn(existingEvent);

        // 2. When
        EventResponseDTO responseDTO = eventService.update(eventId, requestDTO);

        // 3. Then
        assertEquals(eventId, responseDTO.getId());
        assertEquals(requestDTO.getTitle(), responseDTO.getTitle());
        assertEquals(requestDTO.getDescription(), responseDTO.getDescription());
        assertEquals(requestDTO.getCapacity(), responseDTO.getCapacity());
        assertEquals(ownerId, responseDTO.getCreatedByUserId());

    }

    @Test
    void update_ShouldThrowException_WhenUserIsNotOwner() {
        // 1. Given
        UUID ownerId = UUID.randomUUID();
        User owner = new User();
        owner.setId(ownerId);

        UUID eventId = UUID.randomUUID();
        Event existingEvent = new Event();
        existingEvent.setId(eventId);
        existingEvent.setCreatedBy(owner);

        EventRequestDTO requestDTO = new EventRequestDTO();
        requestDTO.setTitle("New Title");
        requestDTO.setCapacity(30);
        requestDTO.setDescription("New Description");

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new UserPrincipal(new User()));

        // When/Then
        assertThrows(ResourceOwnershipException.class, () -> eventService.update(eventId, requestDTO));
        verify(eventRepository).findById(any());
    }


    @Test
    void deleteById() {

        // When
        eventService.deleteById(UUID.randomUUID());

        // Then
        verify(eventRepository).deleteById(any());
    }
}