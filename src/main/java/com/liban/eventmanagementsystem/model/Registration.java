package com.liban.eventmanagementsystem.model;

import lombok.*;

import java.time.LocalDate;
import java.util.Objects;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Registration extends BaseEntity {

    private LocalDate registrationDate;
    private String status;

    private String regisFirstName;
    private String regisLastName;
    private String regisEmail;

    private Event event;

    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registration registration = (Registration) o;
        return getId() != null && Objects.equals(getId(), registration.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
