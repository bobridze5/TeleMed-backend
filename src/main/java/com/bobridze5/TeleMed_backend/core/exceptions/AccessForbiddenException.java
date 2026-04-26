package com.bobridze5.TeleMed_backend.core.exceptions;

import com.bobridze5.TeleMed_backend.api.exceptions.ApiException;
import org.springframework.http.HttpStatus;

public class AccessForbiddenException extends ApiException {
    private static final HttpStatus status = HttpStatus.FORBIDDEN;

    public AccessForbiddenException(String message) {
        super(status, message);
    }
}
