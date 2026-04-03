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
    private String subject;

    @Value("${mail.verification.message}")
    private String message;

    @Value("${mail.verification.template}")
    private String urlTemplate;

    @Override
    public void sendVerificationToken(String recipient, String url, String token) {
        String confirmURL = String.format(urlTemplate, url, token);
        String text = String.format(message, confirmURL);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipient);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);

        log.info("Отправка сообщения по почте: {}", mailMessage.getText());
        mailSender.send(mailMessage);
    }
}
