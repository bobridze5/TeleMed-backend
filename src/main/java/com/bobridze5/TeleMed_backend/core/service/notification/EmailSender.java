package com.bobridze5.TeleMed_backend.core.service.notification;

public interface EmailSender {
    void send(String to, String subject, String body);
}
