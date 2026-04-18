package com.liban.eventmanagementsystem.controller;

import com.liban.eventmanagementsystem.auth.UserPrincipal;
import com.liban.eventmanagementsystem.dtos.request.EventRequestDTO;
import com.liban.eventmanagementsystem.dtos.response.EventResponseDTO;
import com.liban.eventmanagementsystem.model.Event;
import com.liban.eventmanagementsystem.model.User;
import com.liban.eventmanagementsystem.repository.EventRepository;
import com.liban.eventmanagementsystem.services.EventService;
import com.liban.eventmanagementsystem.services.UserServices;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Get a list of events", description = "Allows users to get list of events.")
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Set<EventResponseDTO> findAll() {
        return eventService.getEvents();
    }

    @Operation(summary = "Get event by ID", description = "Allows users to get an event by vent's id.")
    @GetMapping("/{event_id}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDTO getEvent(@PathVariable UUID event_id) {
        return eventService.getById(event_id);
    }

    @Operation(summary = "Create a new event", description = "Allows admins and organizers to create a new event.")
    @PostMapping("")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseDTO createEvent(@Valid @RequestBody EventRequestDTO eventRequestDTO) {
        return eventService.save(eventRequestDTO);
    }

    @Operation(summary = "Update an event", description = "Allows tAdmins and Organizers to edit an event.")
    @PutMapping("/{event_id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public EventResponseDTO updateEvent(@PathVariable UUID event_id,
                                        @Valid @RequestBody EventRequestDTO eventRequestDTO) {

        return eventService.update(event_id, eventRequestDTO);
    }

    @Operation(summary = "Delete an event", description = "Allows admins to delete an event")
    @DeleteMapping("/{event_id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void deleteEvent(@PathVariable UUID event_id) {
        eventService.deleteById(event_id);
    }


}
