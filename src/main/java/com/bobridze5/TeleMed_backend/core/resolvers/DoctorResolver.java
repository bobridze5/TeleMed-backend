package com.bobridze5.TeleMed_backend.core.resolvers;

import com.bobridze5.TeleMed_backend.core.annotations.CurrentDoctor;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.repository.DoctorRepository;
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
public class DoctorResolver implements HandlerMethodArgumentResolver {
    private final DoctorRepository doctorRepository;

    private static final String RESOLVED_DOCTOR_ATTRIBUTE = "RESOLVED_DOCTOR_ENTITY";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentDoctor.class)
                && parameter.getParameterType().equals(Doctor.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        Object cached = webRequest.getAttribute(RESOLVED_DOCTOR_ATTRIBUTE, NativeWebRequest.SCOPE_REQUEST);
        if (cached != null) {
            log.trace("Доктор взят из кеша запроса");
            return cached;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof UserDetailsImpl(User user))) {
            throw new AccessDeniedException("Unauthorized");
        }

        Doctor doctor = doctorRepository.findById(user.getId())
                .orElseThrow(() -> {
                    log.warn("Пользователь {} не является врачом", user.getEmail());
                    return new AccessDeniedException("Пользователь не является врачом");
                });

        webRequest.setAttribute(RESOLVED_DOCTOR_ATTRIBUTE, doctor, NativeWebRequest.SCOPE_REQUEST);
        return doctor;
    }
}
