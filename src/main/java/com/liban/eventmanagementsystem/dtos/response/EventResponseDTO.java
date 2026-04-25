package com.liban.eventmanagementsystem.dtos.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@ToString
public class EventResponseDTO {

    private UUID id;
    private String title;
    private String description;
    private LocalDate StartDate;
    private LocalDate EndDate;
    private LocalTime StartTime;
    private LocalTime EndTime;
    private String location;
    private Integer capacity;
    private String status;

    private UUID createdByUserId;
}
