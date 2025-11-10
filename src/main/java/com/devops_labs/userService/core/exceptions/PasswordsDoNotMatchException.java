package com.devops_labs.userService.core.exceptions;

import com.devops_labs.userService.api.exceptions.ApiException;
import org.springframework.http.HttpStatus;

public class PasswordsDoNotMatchException extends ApiException {
    private static final HttpStatus status = HttpStatus.BAD_REQUEST;

    public PasswordsDoNotMatchException() {
        super(status, "Passwords do not match");
    }

    public PasswordsDoNotMatchException(String message){
      super(status, message);
    }

}
