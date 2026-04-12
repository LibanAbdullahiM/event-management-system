package com.liban.eventmanagementsystem.mapper;

import com.liban.eventmanagementsystem.dtos.request.RegistrationRequestDTO;
import com.liban.eventmanagementsystem.dtos.response.RegistrationResponseDTO;
import com.liban.eventmanagementsystem.model.Registration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {

    @Mapping(target = "event", ignore = true)
    @Mapping(target = "user", ignore = true)
    Registration toRegistration(RegistrationRequestDTO dto);

    @Mapping(target = "event_id", ignore = true)
    @Mapping(target = "user_id", ignore = true)
    RegistrationRequestDTO toRegistrationRequestDTO(Registration registration);

    @Mapping(target = "event_id", ignore = true)
    @Mapping(target = "user_id", ignore = true)
    RegistrationResponseDTO toRegistrationResponseDTO(Registration registration);
}
