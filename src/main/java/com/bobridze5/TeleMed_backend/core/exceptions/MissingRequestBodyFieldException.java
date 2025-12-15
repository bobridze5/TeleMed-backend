package com.bobridze5.TeleMed_backend.core.exceptions;

import com.bobridze5.TeleMed_backend.api.exceptions.ApiException;
import org.springframework.http.HttpStatus;

public class MissingRequestBodyFieldException extends ApiException {
    private final static HttpStatus status = HttpStatus.BAD_REQUEST;

    public MissingRequestBodyFieldException(){
        super(status, "Missing Request Body Fields");
    }

    public MissingRequestBodyFieldException(String message) {
        super(status, message);
    }
}
