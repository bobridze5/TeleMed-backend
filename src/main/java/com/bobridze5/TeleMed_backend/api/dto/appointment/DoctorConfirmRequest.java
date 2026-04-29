package com.bobridze5.TeleMed_backend.api.dto.appointment;

/**
 * Тело запроса при подтверждении приёма врачом.
 * Все поля опциональны: для CHAT, как правило, ничего не требуется,
 * для VIDEO заполняется ссылка, для AUDIO — телефон.
 * meetingNotes используется для пароля или дополнительных инструкций.
 */
public record DoctorConfirmRequest(
        String meetingLink,
        String meetingPhone,
        String meetingNotes
) {
}
