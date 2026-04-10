package com.bobridze5.TeleMed_backend.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mail")
public record MailProperties(
        VerificationTemplate verification,
        AppointmentProps appointment,
        PasswordProps password,
        DoctorProps doctor
) {
    public record VerificationTemplate(String subject, String message, String template) {}

    public record AppointmentProps(AppointmentReminderTemplate reminder) {}
    public record AppointmentReminderTemplate(String subject, String body) {}

    public record PasswordProps(PasswordResetTemplate reset) {}
    public record PasswordResetTemplate(String subject, String message, String template) {}

    public record DoctorProps(DoctorTemplate application, DoctorTemplate approved, DoctorTemplate rejected) {}
    public record DoctorTemplate(String subject, String message) {}
}
