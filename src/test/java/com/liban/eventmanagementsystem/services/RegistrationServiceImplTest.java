package com.liban.eventmanagementsystem.services;

import com.liban.eventmanagementsystem.dtos.request.RegistrationRequestDTO;
import com.liban.eventmanagementsystem.dtos.response.RegistrationResponseDTO;
import com.liban.eventmanagementsystem.exceptions.ResourceNotFoundException;
import com.liban.eventmanagementsystem.mapper.RegistrationMapper;
import com.liban.eventmanagementsystem.mapper.RegistrationMapperImpl;
import com.liban.eventmanagementsystem.model.Event;
import com.liban.eventmanagementsystem.model.Registration;
import com.liban.eventmanagementsystem.model.User;
import com.liban.eventmanagementsystem.repository.EventRepository;
import com.liban.eventmanagementsystem.repository.RegistrationRepository;
import com.liban.eventmanagementsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

    @Mock private RegistrationRepository registrationRepository;
    @Mock private UserRepository userRepository;
    @Mock private EventRepository eventRepository;

    private RegistrationServiceImpl registrationServiceImpl;

    private final RegistrationMapper mapper;

    public RegistrationServiceImplTest() {
        this.mapper = new RegistrationMapperImpl();
    }

    @BeforeEach
    void setUp() {
        registrationServiceImpl = new RegistrationServiceImpl(
                registrationRepository,
                userRepository,
                eventRepository,
                mapper);
    }

    @Test
    void findByUser() {
        // 1. Given
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        Event event = new Event();
        event.setId(UUID.randomUUID());

        Event ev2  = new Event();
        ev2.setId(UUID.randomUUID());

        Registration registration1 = new Registration();
        registration1.setId(UUID.randomUUID());
        registration1.setEvent(event);

        Registration registration2 = new Registration();
        registration2.setId(UUID.randomUUID());
        registration2.setEvent(ev2);

        user.getRegistrations().add(registration1);
        user.getRegistrations().add(registration2);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // 2. When
        Set<RegistrationResponseDTO> responseDTOS = registrationServiceImpl.findByUser(userId);

        // 3. Then
        assertEquals(2, responseDTOS.size());
        assertEquals(userId, responseDTOS.iterator().next().getUser_id());
        verify(userRepository).findById(any());
    }

    @Test
    void createRegistration() {
        // 1. Given
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        UUID eventId = UUID.randomUUID();
        Event event = new Event();
        event.setId(eventId);

        RegistrationRequestDTO requestDTO = new RegistrationRequestDTO();
        requestDTO.setFirstName("Liban");
        requestDTO.setLastName("Abdullahi");
        requestDTO.setUser_id(userId);
        requestDTO.setEvent_id(eventId);

        // Captor the Registration object passed to the repository to verify internal setters
        ArgumentCaptor<Registration> captor = ArgumentCaptor.forClass(Registration.class);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(eventRepository.findById(any())).thenReturn(Optional.of(event));

        Registration registration = mapper.toRegistration(requestDTO);

        when(registrationRepository.save(captor.capture())).thenReturn(registration);

        // 2. When
        RegistrationResponseDTO responseDTO = registrationServiceImpl.createRegistration(requestDTO);

        // Then
        assertEquals(requestDTO.getFirstName(), responseDTO.getFirstName());
        assertEquals(requestDTO.getLastName(), responseDTO.getLastName());

        //Verify the 'save' method was called with the correct object and status
        Registration savedRegistration = captor.getValue();
        assertEquals("Registered", savedRegistration.getStatus());
        assertEquals(LocalDate.now(), savedRegistration.getRegistrationDate());
        assertEquals(user, savedRegistration.getUser());
        assertEquals(event, savedRegistration.getEvent());

    }

    @Test
    void cancelRegistration() {
        // 1. Given
        UUID registrationId = UUID.randomUUID();
        Registration registration = new Registration();
        registration.setId(registrationId);

        when(registrationRepository.findById(any())).thenReturn(Optional.of(registration));

        // 2. When
        registrationServiceImpl.cancelRegistration(registrationId);

        // 3. Then
        verify(registrationRepository).findById(any());
        verify(registrationRepository).delete(any());

    }

    @Test
    void cancelRegistration_ShouldThrowException_WhenNotFound() {
        // 1. Given
        UUID registrationId = UUID.randomUUID();

        when(registrationRepository.findById(any())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> registrationServiceImpl.cancelRegistration(registrationId));

    }
}