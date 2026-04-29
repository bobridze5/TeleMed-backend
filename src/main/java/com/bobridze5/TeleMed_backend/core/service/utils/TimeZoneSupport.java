package com.bobridze5.TeleMed_backend.core.service.utils;

import com.bobridze5.TeleMed_backend.core.entity.auth.User;

import java.time.ZoneId;

public final class TimeZoneSupport {

    public static final ZoneId DEFAULT = ZoneId.of("Europe/Moscow");

    private TimeZoneSupport() {}

    public static ZoneId zoneIdFor(User user) {
        return user != null ? parseOrDefault(user.getTimeZone()) : DEFAULT;
    }

    public static ZoneId parseOrDefault(String raw) {
        if (raw == null || raw.isBlank()) return DEFAULT;
        try {
            return ZoneId.of(raw);
        } catch (Exception e) {
            return DEFAULT;
        }
    }

    public static String validateOrDefaultId(String raw) {
        return parseOrDefault(raw).getId();
    }
}
