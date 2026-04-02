package com.liban.eventmanagementsystem.model;

import jakarta.persistence.MappedSuperclass;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class Person extends BaseEntity {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
