package com.liban.eventmanagementsystem.dtos.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RegistrationRequestDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private UUID event_id;
    private UUID user_id;
}
