package com.liban.eventmanagementsystem.dtos.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@ToString
public class RegistrationResponseDTO {

    private UUID id;
    private LocalDate registrationDate;
    private String status;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private UUID event_id;
    private UUID user_id;
}
