package com.liban.eventmanagementsystem.repository;

import com.liban.eventmanagementsystem.model.Event;
import com.liban.eventmanagementsystem.model.User;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> findByCreatedBy(User createdBy);
}
