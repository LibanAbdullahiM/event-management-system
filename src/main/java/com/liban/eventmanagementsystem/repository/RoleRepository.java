package com.liban.eventmanagementsystem.repository;

import com.liban.eventmanagementsystem.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT role FROM Role role WHERE role.role=:role")
    Role findByRole(@Param("role") String role);
}
