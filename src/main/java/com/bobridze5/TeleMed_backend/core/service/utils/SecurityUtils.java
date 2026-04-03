package com.bobridze5.TeleMed_backend.core.service.utils;

import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.security.UserDetailsImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    public static User getAuthCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Пользователь не авторизован");
        }

        Object principial = auth.getPrincipal();

        if (principial instanceof UserDetailsImpl(User user)) {
            return user;
        }

        throw new AccessDeniedException("Данные пользователя не найдены в контексте безопасности");
    }
}
