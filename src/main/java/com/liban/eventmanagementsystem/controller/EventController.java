package com.liban.eventmanagementsystem.controller;

import com.liban.eventmanagementsystem.auth.UserPrincipal;
import com.liban.eventmanagementsystem.dtos.request.EventRequestDTO;
import com.liban.eventmanagementsystem.dtos.response.EventResponseDTO;
import com.liban.eventmanagementsystem.model.Event;
import com.liban.eventmanagementsystem.model.User;
import com.liban.eventmanagementsystem.repository.EventRepository;
import com.liban.eventmanagementsystem.services.EventService;
import com.liban.eventmanagementsystem.services.UserServices;
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

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Set<EventResponseDTO> findAll() {
        return eventService.getEvents();
    }

    @GetMapping("/{event_id}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDTO getEvent(@PathVariable UUID event_id) {
        return eventService.getById(event_id);
    }

    @PostMapping("")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseDTO createEvent(@RequestBody EventRequestDTO eventRequestDTO) {
        return eventService.save(eventRequestDTO);
    }

    @PutMapping("/{event_id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public EventResponseDTO updateEvent(@PathVariable UUID event_id,
                                        @RequestBody EventRequestDTO eventRequestDTO) {

        return eventService.update(event_id, eventRequestDTO);
    }

    @DeleteMapping("/{event_id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void deleteEvent(@PathVariable UUID event_id) {
        eventService.deleteById(event_id);
    }


}
