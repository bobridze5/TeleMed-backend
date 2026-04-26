package com.bobridze5.TeleMed_backend.api.dto.chat;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        Long chatId,
        Long senderId,
        String senderName,
        String content,
        String attachmentName,
        String attachmentMime,
        Long attachmentSize,
        String attachmentUrl,
        LocalDateTime sentAt,
        LocalDateTime editedAt
) {
}
