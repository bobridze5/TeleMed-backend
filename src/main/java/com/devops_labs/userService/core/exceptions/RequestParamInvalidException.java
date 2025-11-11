package com.devops_labs.userService.core.exceptions;

import com.devops_labs.userService.api.exceptions.ApiException;
import org.springframework.http.HttpStatus;

public class RequestParamInvalidException extends ApiException {
    private static final HttpStatus status = HttpStatus.BAD_REQUEST;

    public RequestParamInvalidException(){
        super(status,"Incorrect request parameter");
    }

    public RequestParamInvalidException(String message) {
        super(status, message);
    }
}
