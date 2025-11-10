package com.devops_labs.userService.core.exceptions;

import com.devops_labs.userService.api.exceptions.ApiException;
import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends ApiException {
    private static final HttpStatus status = HttpStatus.NOT_FOUND;

    public EntityNotFoundException(){
        super(status, "Entity not found");
    }

    public EntityNotFoundException(String message) {
        super(status, message);
    }
}
