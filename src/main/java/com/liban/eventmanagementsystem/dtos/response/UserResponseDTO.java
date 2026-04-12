package com.liban.eventmanagementsystem.dtos.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
public class UserResponseDTO {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String username;

    private Set<String> roles = new HashSet<>();
}
