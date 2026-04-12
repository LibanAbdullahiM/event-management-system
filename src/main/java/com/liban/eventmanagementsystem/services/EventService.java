package com.liban.eventmanagementsystem.services;

import com.liban.eventmanagementsystem.dtos.request.EventRequestDTO;
import com.liban.eventmanagementsystem.dtos.response.EventResponseDTO;
import com.liban.eventmanagementsystem.model.Event;
import com.liban.eventmanagementsystem.model.User;

import java.util.Set;
import java.util.UUID;

public interface EventService {

    Set<EventResponseDTO> getEvents();

    EventResponseDTO getById(UUID id);

    EventResponseDTO save(EventRequestDTO eventRequestDTO);

    EventResponseDTO update(UUID event_id, EventRequestDTO eventRequestDTO);

    void deleteById(UUID id);
}
