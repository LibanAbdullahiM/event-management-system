package com.liban.eventmanagementsystem.repository;

import com.liban.eventmanagementsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT user FROM User user WHERE user.username=:username")
    User findByUsername(@Param("username") String username);

    @Query("SELECT user FROM User user WHERE user.username=:email")
    User findByEmail(@Param("email") String email);
}
