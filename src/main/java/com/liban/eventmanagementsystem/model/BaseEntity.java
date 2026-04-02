package com.liban.eventmanagementsystem.model;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class BaseEntity {

    private UUID id;
}
