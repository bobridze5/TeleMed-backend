package com.bobridze5.TeleMed_backend.core.resolvers;

import com.bobridze5.TeleMed_backend.core.annotations.CurrentAdmin;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.medical.Admin;
import com.bobridze5.TeleMed_backend.core.repository.AdminRepository;
import com.bobridze5.TeleMed_backend.core.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminResolver implements HandlerMethodArgumentResolver {
    private final AdminRepository adminRepository;

    private static final String RESOLVED_ADMIN_ATTRIBUTE = "RESOLVED_ADMIN_ENTITY";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentAdmin.class)
                && parameter.getParameterType().equals(Admin.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        Object cached = webRequest.getAttribute(RESOLVED_ADMIN_ATTRIBUTE, NativeWebRequest.SCOPE_REQUEST);
        if (cached != null) return cached;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetailsImpl(User user))) {
            throw new AccessDeniedException("Unauthorized");
        }

        Admin admin = adminRepository.findById(user.getId())
                .orElseThrow(() -> {
                    log.warn("Пользователь {} не является администратором", user.getEmail());
                    return new AccessDeniedException("Пользователь не является администратором");
                });

        webRequest.setAttribute(RESOLVED_ADMIN_ATTRIBUTE, admin, NativeWebRequest.SCOPE_REQUEST);
        return admin;
    }
}
