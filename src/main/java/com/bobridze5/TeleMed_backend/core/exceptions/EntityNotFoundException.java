package com.bobridze5.TeleMed_backend.core.exceptions;

import com.bobridze5.TeleMed_backend.api.exceptions.ApiException;
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
