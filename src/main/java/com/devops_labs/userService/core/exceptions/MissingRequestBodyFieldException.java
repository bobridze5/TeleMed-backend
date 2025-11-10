package com.devops_labs.userService.core.exceptions;

import com.devops_labs.userService.api.exceptions.ApiException;
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
