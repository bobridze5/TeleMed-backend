package com.bobridze5.TeleMed_backend.api.controllers.chat;

import com.bobridze5.TeleMed_backend.api.dto.chat.MessageRequest;
import com.bobridze5.TeleMed_backend.core.service.chat.ChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Tag(name = "WebSocket чат")
public class ChatWebSocketController {

    private final ChatService chatService;

    /**
     * Клиент отправляет на /app/chat/{chatId}/send
     * Сервис сохраняет сообщение и бродкастит на /topic/chat/{chatId}
     */
    @MessageMapping("/chat/{chatId}/send")
    public void sendMessage(
            @DestinationVariable Long chatId,
            @Payload @Valid MessageRequest request,
            Principal principal
    ) {
        chatService.sendMessageWs(chatId, principal, request);
    }
}
