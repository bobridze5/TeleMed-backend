package com.bobridze5.TeleMed_backend.core.service.auth;

import com.bobridze5.TeleMed_backend.core.config.MailProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final MailProperties mail;

    @Override
    public void sendVerificationToken(String recipient, String url, String token) {
        String confirmURL = String.format(mail.verification().template(), url, token);
        String text = String.format(mail.verification().message(), confirmURL);
        send(recipient, mail.verification().subject(), text);
    }

    @Override
    public void sendPasswordResetEmail(String recipient, String url, String token) {
        String resetURL = String.format(mail.password().reset().template(), url, token);
        String text = String.format(mail.password().reset().message(), resetURL);
        send(recipient, mail.password().reset().subject(), text);
    }

    @Override
    public void sendDoctorApplicationReceived(String recipient, String doctorName) {
        String text = String.format(mail.doctor().application().message(), doctorName);
        send(recipient, mail.doctor().application().subject(), text);
    }

    @Override
    public void sendDoctorApproved(String recipient, String doctorName) {
        String text = String.format(mail.doctor().approved().message(), doctorName);
        send(recipient, mail.doctor().approved().subject(), text);
    }

    @Override
    public void sendDoctorRejected(String recipient, String doctorName, String reason) {
        String text = String.format(mail.doctor().rejected().message(), doctorName, reason);
        send(recipient, mail.doctor().rejected().subject(), text);
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
