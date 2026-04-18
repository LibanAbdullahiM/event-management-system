package com.liban.eventmanagementsystem.controller;

import com.liban.eventmanagementsystem.dtos.request.RegistrationRequestDTO;
import com.liban.eventmanagementsystem.dtos.response.RegistrationResponseDTO;
import com.liban.eventmanagementsystem.services.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/{user_id}/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @Operation(summary = "Get registered event", description = "Allows users to get the event they registered to.")
    @GetMapping("")
    public Set<RegistrationResponseDTO> getRegistrationsByUser(@PathVariable UUID user_id) {
        return registrationService.findByUser(user_id);
    }

    @Operation(summary = "register to an event", description = "Allows the users to register an event.")
    @PostMapping("/{event_id}/register")
    public RegistrationResponseDTO createRegistration(@PathVariable UUID user_id,
                                                      @PathVariable UUID event_id,
                                                      @Valid @RequestBody RegistrationRequestDTO registrationRequestDTO) {
        registrationRequestDTO.setEvent_id(event_id);
        registrationRequestDTO.setUser_id(user_id);

        return registrationService.createRegistration(registrationRequestDTO);
    }

    @Operation(summary = "Cancel registered event", description = "Allows the user to cancel the event they registered.")
    @DeleteMapping("/{registration_id}/delete")
    @ResponseStatus(HttpStatus.OK)
    public void cancelRegistration(@PathVariable UUID user_id,
                                   @PathVariable UUID registration_id) {
        registrationService.cancelRegistration(registration_id);
    }
}
