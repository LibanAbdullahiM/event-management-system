package com.liban.eventmanagementsystem.model;

import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    private String name;
    private String email;
    private String password;

    private Set<Role> roles =  new HashSet<Role>();

    private Set<Event> events = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
