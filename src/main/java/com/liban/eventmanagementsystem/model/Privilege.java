package com.liban.eventmanagementsystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "privileges")
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String privilege;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "privileges")
    private Set<Role> roles =  new HashSet<>();
}
