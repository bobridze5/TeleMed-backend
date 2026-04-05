package com.bobridze5.TeleMed_backend.core.entity.auth;

public enum UserStatus {
    ACTIVE,
    INACTIVE,
    PENDING,           // Email не подтверждён (пациент)
    AWAITING_APPROVAL, // Ожидает проверки администратором (врач)
    BANNED
}
