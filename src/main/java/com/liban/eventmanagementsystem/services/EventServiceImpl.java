package com.liban.eventmanagementsystem.services;

import com.liban.eventmanagementsystem.auth.UserPrincipal;
import com.liban.eventmanagementsystem.dtos.request.EventRequestDTO;
import com.liban.eventmanagementsystem.dtos.response.EventResponseDTO;
import com.liban.eventmanagementsystem.exceptions.ResourceNotFoundException;
import com.liban.eventmanagementsystem.mapper.EventMapper;
import com.liban.eventmanagementsystem.model.Event;
import com.liban.eventmanagementsystem.model.User;
import com.liban.eventmanagementsystem.repository.EventRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventServiceImpl(EventRepository eventRepository,
                            EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public Set<EventResponseDTO> getEvents() {

        Set<Event> events = new HashSet<>(eventRepository.findAll());

        Set<EventResponseDTO> eventResponseDTOS = new HashSet<>();

        for (Event event : events) {
            eventResponseDTOS.add(eventMapper.toEventResponseDTO(event));
        }

        return eventResponseDTOS;
    }

    @Override
    public EventResponseDTO getById(UUID id) {
        Optional<Event> optionalEvent = eventRepository.findById(id);

        if(optionalEvent.isEmpty()) {
            throw new ResourceNotFoundException("Event not found");
        }

        return eventMapper.toEventResponseDTO(optionalEvent.get());
    }

    @Override
    public EventResponseDTO save(EventRequestDTO eventRequestDTO) {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();

        Event event = eventMapper.toEvent(eventRequestDTO);

        event.setCreatedBy(user);
        event.setStatus("Open");

        return eventMapper.toEventResponseDTO(eventRepository.save(event));
    }

    @Override
    public EventResponseDTO update(UUID event_id, EventRequestDTO eventRequestDTO) {

        Event event = eventRepository.findById(event_id).orElse(null);

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();

        User created_user = event.getCreatedBy();

        if(!user.equals(created_user)) {
            try {
                throw new IllegalAccessException("You are not allowed to update this event");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        event.setTitle(eventRequestDTO.getTitle());
        event.setDescription(eventRequestDTO.getDescription());
        event.setStartDate(eventRequestDTO.getStartDate());
        event.setEndDate(eventRequestDTO.getEndDate());
        event.setStartTime(eventRequestDTO.getStartTime());
        event.setEndTime(eventRequestDTO.getEndTime());
        event.setLocation(eventRequestDTO.getLocation());
        event.setCapacity(eventRequestDTO.getCapacity());

        return eventMapper.toEventResponseDTO(eventRepository.save(event));
    }

    @Override
    public void deleteById(UUID id) {
        eventRepository.deleteById(id);
    }
}
