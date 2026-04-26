package com.bobridze5.TeleMed_backend.api.dto.chat;

import java.time.LocalDateTime;
import java.util.List;

public record ChatResponse(
        Long id,
        List<ChatParticipantDto> participants,
        LocalDateTime createdAt
) {
}
