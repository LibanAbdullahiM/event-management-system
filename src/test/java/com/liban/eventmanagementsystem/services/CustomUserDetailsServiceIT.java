package com.liban.eventmanagementsystem.services;

import com.liban.eventmanagementsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomUserDetailsServiceIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void loadUserByUsername() {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("liban");

        assertNotNull(userDetails);
        assertEquals("liban", userDetails.getUsername());

    }
}