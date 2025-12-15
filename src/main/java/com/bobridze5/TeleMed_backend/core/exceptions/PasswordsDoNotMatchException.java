package com.bobridze5.TeleMed_backend.core.exceptions;

import com.bobridze5.TeleMed_backend.api.exceptions.ApiException;
import org.springframework.http.HttpStatus;

public class PasswordsDoNotMatchException extends ApiException {
    private static final HttpStatus status = HttpStatus.BAD_REQUEST;

    public PasswordsDoNotMatchException() {
        super(status, "Passwords do not match");
    }

    public PasswordsDoNotMatchException(String message){
      super(status, message);
    }

    public PasswordsDoNotMatchException(HttpStatus status, String message){
        super(status, message);
    }

}
