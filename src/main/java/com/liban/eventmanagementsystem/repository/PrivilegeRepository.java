package com.liban.eventmanagementsystem.repository;

import com.liban.eventmanagementsystem.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
}
