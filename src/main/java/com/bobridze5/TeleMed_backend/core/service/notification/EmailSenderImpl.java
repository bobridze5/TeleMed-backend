package com.bobridze5.TeleMed_backend.core.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderImpl implements EmailSender {
    private final JavaMailSender mailSender;

    @Override
    public void send(String to, String subject, String body) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);

        log.info("Отправка письма на адрес: {} с темой: {}", to, subject);
        mailSender.send(mailMessage);
        log.info("Письмо успешно отправлено на адрес: {}", to);
    }
}
