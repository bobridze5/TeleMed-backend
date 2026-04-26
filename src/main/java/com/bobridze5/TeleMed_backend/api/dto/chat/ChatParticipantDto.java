package com.bobridze5.TeleMed_backend.api.dto.chat;

public record ChatParticipantDto(
        Long userId,
        String fullName,
        String role
) {
}
