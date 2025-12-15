package com.bobridze5.TeleMed_backend.core.exceptions;

import com.bobridze5.TeleMed_backend.api.exceptions.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends ApiException {
    private static final HttpStatus status = HttpStatus.UNAUTHORIZED;

    public InvalidTokenException(){
        super(status, "Invalid Token");
    }

    public InvalidTokenException(String message) {
        super(status, message);
    }
}
