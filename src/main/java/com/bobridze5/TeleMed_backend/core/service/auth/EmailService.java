package com.bobridze5.TeleMed_backend.core.service.auth;

public interface EmailService {
    void sendVerificationToken(String recipient, String url, String token);

    void sendPasswordResetEmail(String recipient, String url, String token);

    void sendDoctorApplicationReceived(String recipient, String doctorName);

    void sendDoctorApproved(String recipient, String doctorName);

    void sendDoctorRejected(String recipient, String doctorName, String reason);
}
