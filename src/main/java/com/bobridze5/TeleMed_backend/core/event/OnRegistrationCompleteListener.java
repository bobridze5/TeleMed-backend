package com.bobridze5.TeleMed_backend.core.event;

import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.service.auth.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OnRegistrationCompleteListener {
    private final VerificationTokenService verificationTokenService;
    private final JavaMailSender mailSender;

    @Async
    @Retryable(
            retryFor = {MailSendException.class},
            maxAttempts = 2,
            backoff = @Backoff(delay = 5000)
    )
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleEventRegistrationComplete(OnRegistrationCompleteEvent event) {
        // TODO: вынести логику в mail сервис
        log.info("Попытка обработки события для user_id = {}", event.getUser().getId());
        User user = event.getUser();
        String baseURL = event.getUrl();
        String token = UUID.randomUUID().toString();

        verificationTokenService.createToken(user);

        String recipientAddress = user.getEmail();
        String subject = "Подтверждение регистрации";
        String confirmURL = baseURL + "registrationConfirm?token=" + token;
        String message = "Перейдите по ссылке ниже, чтобы подтвердить электронную почту: \r\n " + confirmURL;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipientAddress);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        log.info("Отправка сообщения по почте: {}", message);
        mailSender.send(mailMessage);
    }
}
