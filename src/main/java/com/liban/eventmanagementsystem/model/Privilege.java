package com.liban.eventmanagementsystem.model;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Privilege {

    private Long id;

    private String privilege;

    private Set<Role> roles =  new HashSet<Role>();
}
