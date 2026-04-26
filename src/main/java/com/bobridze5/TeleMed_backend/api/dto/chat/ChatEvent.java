package com.bobridze5.TeleMed_backend.api.dto.chat;

public record ChatEvent(
        String type,        // "NEW" | "EDITED" | "DELETED"
        MessageResponse message
) {
}
