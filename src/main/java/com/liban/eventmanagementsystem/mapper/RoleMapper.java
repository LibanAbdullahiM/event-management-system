package com.liban.eventmanagementsystem.mapper;

import com.liban.eventmanagementsystem.dtos.request.RoleRequestDTO;
import com.liban.eventmanagementsystem.model.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role toRole(RoleRequestDTO  roleRequestDTO);

    RoleRequestDTO toRoleRequestDTO(Role role);
}
