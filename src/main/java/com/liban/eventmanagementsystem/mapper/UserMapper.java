package com.liban.eventmanagementsystem.mapper;

import com.liban.eventmanagementsystem.dtos.request.UserRequestDTO;
import com.liban.eventmanagementsystem.dtos.response.UserResponseDTO;
import com.liban.eventmanagementsystem.model.Role;
import com.liban.eventmanagementsystem.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserRequestDTO toUserRequestDTO(User user);

    User toUser(UserRequestDTO userRequestDTO);

    @Mapping(target = "roles", source = "roles")
    UserResponseDTO toUserResponseDTO(User user);

    default String map(Role role) {
        return role.getRole();
    }
}
