package com.devops_labs.userService.core.exceptions;

import com.devops_labs.userService.api.exceptions.ApiException;
import org.springframework.http.HttpStatus;

public class EntityAlreadyExistsException extends ApiException {
    private static final HttpStatus status = HttpStatus.CONFLICT;

    public EntityAlreadyExistsException(){
        super(status, "Entity already exists");
    }

    public EntityAlreadyExistsException(String message) {
        super(status, message);
    }
}
