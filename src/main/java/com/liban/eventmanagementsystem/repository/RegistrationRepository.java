package com.liban.eventmanagementsystem.repository;

import com.liban.eventmanagementsystem.model.Registration;
import com.liban.eventmanagementsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface RegistrationRepository extends JpaRepository<Registration, UUID> {

    List<Registration> findByUser(User user);
}
