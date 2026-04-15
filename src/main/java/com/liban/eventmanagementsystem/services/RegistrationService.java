package com.liban.eventmanagementsystem.services;

import com.liban.eventmanagementsystem.dtos.request.RegistrationRequestDTO;
import com.liban.eventmanagementsystem.dtos.request.UserRequestDTO;
import com.liban.eventmanagementsystem.dtos.response.RegistrationResponseDTO;
import com.liban.eventmanagementsystem.model.Registration;
import com.liban.eventmanagementsystem.model.User;

import java.util.Set;
import java.util.UUID;

public interface RegistrationService {

    Set<RegistrationResponseDTO> findByUser(UUID user_id);

    RegistrationResponseDTO createRegistration(RegistrationRequestDTO registrationRequestDTO);

    void cancelRegistration(UUID id);


}
