package com.liban.eventmanagementsystem.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class EventRequestDTO {

    private String title;
    private String description;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate startDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate endDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private String location;
    private int capacity;
}
