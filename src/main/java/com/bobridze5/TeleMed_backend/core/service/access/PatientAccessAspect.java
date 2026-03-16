package com.bobridze5.TeleMed_backend.core.service.access;

import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.service.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PatientAccessAspect {
    private final AccessControlService accessControlService;

    public void check(Long patientId) {
        User user = SecurityUtils.getAuthCurrentUser();
        accessControlService.checkAccess(user, patientId);
    }
}
