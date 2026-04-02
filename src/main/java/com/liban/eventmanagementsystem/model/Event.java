package com.liban.eventmanagementsystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "events")
public class Event extends BaseEntity {

    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private int capacity;
    private String status;

    @OneToMany(mappedBy = "event")
    private Set<Registration> registrations =  new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User createdBy;

    @Override
    public boolean equals(Object o) {
        if (this ==  o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return getId() != null && Objects.equals(getId(), event.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
