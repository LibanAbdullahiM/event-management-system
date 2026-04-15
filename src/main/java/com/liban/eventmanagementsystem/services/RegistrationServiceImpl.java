package com.liban.eventmanagementsystem.services;

import com.liban.eventmanagementsystem.dtos.request.RegistrationRequestDTO;
import com.liban.eventmanagementsystem.dtos.request.UserRequestDTO;
import com.liban.eventmanagementsystem.dtos.response.RegistrationResponseDTO;
import com.liban.eventmanagementsystem.mapper.RegistrationMapper;
import com.liban.eventmanagementsystem.model.Event;
import com.liban.eventmanagementsystem.model.Registration;
import com.liban.eventmanagementsystem.model.User;
import com.liban.eventmanagementsystem.repository.EventRepository;
import com.liban.eventmanagementsystem.repository.RegistrationRepository;
import com.liban.eventmanagementsystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RegistrationMapper mapper;

    public RegistrationServiceImpl(RegistrationRepository registrationRepository,
                                   UserRepository userRepository,
                                   EventRepository eventRepository,
                                   RegistrationMapper mapper) {
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.mapper = mapper;
    }

    @Override
    public Set<RegistrationResponseDTO> findByUser(UUID user_id) {
        User user = userRepository.findById(user_id).orElse(null);

        Set<Registration> registrations = user.getRegistrations();

        Set<RegistrationResponseDTO> dtos = new HashSet<>();

        for (Registration registration : registrations) {
            RegistrationResponseDTO dto = mapper.toRegistrationResponseDTO(registration);
            dto.setUser_id(user_id);
            dto.setEvent_id(registration.getEvent().getId());
            dtos.add(dto);
        }

        return dtos;
    }

    @Override
    public RegistrationResponseDTO createRegistration(RegistrationRequestDTO registrationRequestDTO) {

        User user = userRepository.findById(registrationRequestDTO.getUser_id()).orElse(null);

        Event event = eventRepository.findById(registrationRequestDTO.getEvent_id()).orElse(null);

        Registration registration = mapper.toRegistration(registrationRequestDTO);
        registration.setUser(user);
        registration.setEvent(event);
        registration.setStatus("Registered");
        registration.setRegistrationDate(LocalDate.now());

        RegistrationResponseDTO savedDto = mapper.toRegistrationResponseDTO(registrationRepository.save(registration));
        savedDto.setEvent_id(registration.getEvent().getId());
        savedDto.setUser_id(registration.getUser().getId());

        return savedDto;
    }

    @Override
    public void cancelRegistration(UUID id) {
        Optional<Registration> optionalRegistration = registrationRepository.findById(id);

        if(optionalRegistration.isEmpty()) {
            throw new RuntimeException("Registration not found");
        }

        registrationRepository.delete(optionalRegistration.get());
    }
}
