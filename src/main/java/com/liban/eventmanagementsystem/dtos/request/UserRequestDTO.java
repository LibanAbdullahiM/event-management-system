package com.liban.eventmanagementsystem.dtos.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private String username;
    private String password;
}
