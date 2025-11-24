package com.devops_labs.userService.core.event;

import com.devops_labs.userService.core.entity.User;
import com.devops_labs.userService.core.service.interfaces.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OnRegistrationCompleteListener  {
    private final VerificationTokenService verificationTokenService;
    private final JavaMailSender mailSender;

    @EventListener
    public void handleEventRegistrationComplete(OnRegistrationCompleteEvent event) {
        // TODO: вынести логику в mail сервис
        User user = event.getUser();
        String baseURL = event.getUrl();
        String token = UUID.randomUUID().toString();

        verificationTokenService.createToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = "Подтверждение регистрации";
        String confirmURL = baseURL + "registrationConfirm?token=" + token;
        String message = "Перейдите по ссылке ниже, чтобы подтвердить электронную почту: ";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipientAddress);
        mailMessage.setSubject(subject);
        mailMessage.setText(message + "\r\n" + confirmURL);
        mailSender.send(mailMessage);
    }
}
