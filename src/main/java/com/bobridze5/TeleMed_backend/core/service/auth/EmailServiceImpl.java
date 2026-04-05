package com.bobridze5.TeleMed_backend.core.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${mail.verification.subject}")
    private String verificationSubject;

    @Value("${mail.verification.message}")
    private String verificationMessage;

    @Value("${mail.verification.template}")
    private String verificationUrlTemplate;

    @Value("${mail.doctor.application.subject}")
    private String doctorApplicationSubject;

    @Value("${mail.doctor.application.message}")
    private String doctorApplicationMessage;

    @Value("${mail.doctor.approved.subject}")
    private String doctorApprovedSubject;

    @Value("${mail.doctor.approved.message}")
    private String doctorApprovedMessage;

    @Value("${mail.doctor.rejected.subject}")
    private String doctorRejectedSubject;

    @Value("${mail.doctor.rejected.message}")
    private String doctorRejectedMessage;

    @Override
    public void sendVerificationToken(String recipient, String url, String token) {
        String confirmURL = String.format(verificationUrlTemplate, url, token);
        String text = String.format(verificationMessage, confirmURL);
        send(recipient, verificationSubject, text);
    }

    @Override
    public void sendDoctorApplicationReceived(String recipient, String doctorName) {
        String text = String.format(doctorApplicationMessage, doctorName);
        send(recipient, doctorApplicationSubject, text);
    }

    @Override
    public void sendDoctorApproved(String recipient, String doctorName) {
        String text = String.format(doctorApprovedMessage, doctorName);
        send(recipient, doctorApprovedSubject, text);
    }

    @Override
    public void sendDoctorRejected(String recipient, String doctorName, String reason) {
        String text = String.format(doctorRejectedMessage, doctorName, reason);
        send(recipient, doctorRejectedSubject, text);
    }

    private void send(String recipient, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(text);
        log.info("Отправка письма на {}: {}", recipient, subject);
        mailSender.send(message);
    }
}
