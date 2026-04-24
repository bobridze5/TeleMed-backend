package com.bobridze5.TeleMed_backend.api.dto.admin;

public record AdminStatsResponse(
        long totalPatients,
        long totalDoctors,
        long pendingDoctors,
        long activeDoctors,
        long bannedUsers
) {
}
