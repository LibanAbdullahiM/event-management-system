package com.liban.eventmanagementsystem.services;

import com.liban.eventmanagementsystem.auth.UserPrincipal;
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

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Set<Event> getEvents() {
        return new HashSet<>(eventRepository.findAll());
    }

    @Override
    public Event getById(UUID id) {
        Optional<Event> optionalEvent = eventRepository.findById(id);

        if(optionalEvent.isEmpty()) {
            throw new RuntimeException("Event not found");
        }

        return optionalEvent.get();
    }

    @Override
    public Event save(Event event) {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();

        event.setCreatedBy(user);

        return eventRepository.save(event);
    }

    @Override
    public Event update(Event event) {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();

        User created_user = event.getCreatedBy();

        System.out.println("createdUser");
        System.out.println(created_user);

        if(!user.equals(created_user)) {
            try {
                throw new IllegalAccessException("You are not allowed to update this event");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return eventRepository.save(event);
    }

    @Override
    public void deleteById(UUID id) {
        eventRepository.deleteById(id);
    }

    @Override
    public void delete(Event event) {
        eventRepository.delete(event);
    }
}
