package com.bobridze5.TeleMed_backend.core.service.auth;

public interface EmailService {
    void sendVerificationToken(String recipient, String url, String token);
}
