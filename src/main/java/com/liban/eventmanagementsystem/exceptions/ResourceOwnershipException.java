package com.liban.eventmanagementsystem.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ResourceOwnershipException extends RuntimeException{

    public ResourceOwnershipException(String message) {
        super(message);
    }

    public ResourceOwnershipException(String message, Throwable cause) {
        super(message, cause);
    }
}
