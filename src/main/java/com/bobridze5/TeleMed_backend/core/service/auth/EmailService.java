package com.bobridze5.TeleMed_backend.core.service.auth;

import com.bobridze5.TeleMed_backend.core.config.MailProperties;
import com.bobridze5.TeleMed_backend.core.service.notification.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Высокоуровневый email-сервис auth- и admin-флоу: знает про шаблоны
 * писем (`MailProperties`) и формирует тело сообщений. Саму отправку
 * делегирует {@link EmailSender}, чтобы не дублировать обвязку JavaMailSender.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final EmailSender emailSender;
    private final MailProperties mail;

    public void sendVerificationToken(String recipient, String url, String token) {
        String confirmURL = String.format(mail.verification().template(), url, token);
        String text = String.format(mail.verification().message(), confirmURL);
        emailSender.send(recipient, mail.verification().subject(), text);
    }

    public void sendPasswordResetEmail(String recipient, String url, String token) {
        String resetURL = String.format(mail.password().reset().template(), url, token);
        String text = String.format(mail.password().reset().message(), resetURL);
        emailSender.send(recipient, mail.password().reset().subject(), text);
    }

    public void sendDoctorApplicationReceived(String recipient, String doctorName) {
        String text = String.format(mail.doctor().application().message(), doctorName);
        emailSender.send(recipient, mail.doctor().application().subject(), text);
    }

    public void sendDoctorApproved(String recipient, String doctorName) {
        String text = String.format(mail.doctor().approved().message(), doctorName);
        emailSender.send(recipient, mail.doctor().approved().subject(), text);
    }

    public void sendDoctorRejected(String recipient, String doctorName, String reason) {
        String text = String.format(mail.doctor().rejected().message(), doctorName, reason);
        emailSender.send(recipient, mail.doctor().rejected().subject(), text);
    }
}
