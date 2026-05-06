package com.bobridze5.TeleMed_backend.core.exceptions;

import com.bobridze5.TeleMed_backend.api.exceptions.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Бросается при попытке выполнить над записью на приём операцию,
 * не разрешённую её текущим состоянием (например, изменить время уже
 * подтверждённого приёма, отменить уже отменённый и т.п.).
 */
public class AppointmentStateException extends ApiException {
    private static final HttpStatus status = HttpStatus.CONFLICT;

    public AppointmentStateException(String message) {
        super(status, message);
    }
}
