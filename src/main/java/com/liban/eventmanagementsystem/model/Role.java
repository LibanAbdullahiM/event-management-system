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
public class Role {

    private Long id;

    private String role;

    private Set<Privilege> privileges = new HashSet<Privilege>();

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Role role1 = (Role) o;
        return getId() != null && Objects.equals(getId(), role1.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
