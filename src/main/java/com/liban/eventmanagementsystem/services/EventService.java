package com.liban.eventmanagementsystem.services;

import com.liban.eventmanagementsystem.model.Event;
import com.liban.eventmanagementsystem.model.User;

import java.util.Set;
import java.util.UUID;

public interface EventService {

    Set<Event> getEvents();

    Event getById(UUID id);

    Event save(Event event);

    Event update(Event event);

    void deleteById(UUID id);

    void delete(Event event);
}
