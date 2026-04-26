package com.bobridze5.TeleMed_backend.core.resolvers;

import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.repository.PatientRepository;
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
public class PatientResolver implements HandlerMethodArgumentResolver {
    private final PatientRepository patientRepository;

    private static final String RESOLVED_PATIENT_ATTRIBUTE = "RESOLVED_PATIENT_ENTITY";


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentPatient.class)
                && parameter.getParameterType().equals(Patient.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {

        // TODO: проверить кеширование
        Object cachedPatient = webRequest.getAttribute(RESOLVED_PATIENT_ATTRIBUTE, NativeWebRequest.SCOPE_REQUEST);
        if (cachedPatient != null) {
            log.trace("Patient взят из кеша запроса");
            return cachedPatient;
        }


        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof UserDetailsImpl(User user))) {
            throw new AccessDeniedException("Unauthorized");
        }

        Patient patient = patientRepository.findById(user.getId())
                .orElseThrow(() -> {
                    log.warn("Пользователь {} не является пациентом", user.getEmail());
                    return new AccessDeniedException("Пользователь не пациент");
                });

        webRequest.setAttribute(RESOLVED_PATIENT_ATTRIBUTE, patient, NativeWebRequest.SCOPE_REQUEST);


        return patient;
    }
}
