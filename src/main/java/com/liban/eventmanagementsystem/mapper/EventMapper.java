package com.liban.eventmanagementsystem.mapper;

import com.liban.eventmanagementsystem.dtos.request.EventRequestDTO;
import com.liban.eventmanagementsystem.dtos.response.EventResponseDTO;
import com.liban.eventmanagementsystem.model.Event;
import com.liban.eventmanagementsystem.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventRequestDTO  toEventRequestDTO(Event event);

    Event toEvent(EventRequestDTO eventRequestDTO);

    @Mapping(source = "createdBy", target = "createdByUserId")
    EventResponseDTO toEventResponseDTO(Event event);

    default UUID map(User user) {
        if(user == null) return null;
        return user.getId();
    }
}
