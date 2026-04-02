package com.liban.eventmanagementsystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "registrations")
public class Registration extends BaseEntity {

    private LocalDate registrationDate;
    private String status;

    private String regisFirstName;
    private String regisLastName;
    private String regisEmail;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id")
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
