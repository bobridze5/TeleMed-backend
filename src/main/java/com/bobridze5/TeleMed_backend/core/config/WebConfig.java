package com.bobridze5.TeleMed_backend.core.config;

import com.bobridze5.TeleMed_backend.core.resolvers.AdminResolver;
import com.bobridze5.TeleMed_backend.core.resolvers.DoctorResolver;
import com.bobridze5.TeleMed_backend.core.resolvers.PatientResolver;
import com.bobridze5.TeleMed_backend.core.resolvers.UserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final PatientResolver patientResolver;
    private final DoctorResolver doctorResolver;
    private final AdminResolver adminResolver;
    private final UserResolver userResolver;

    @Value("${app.cors.allowed-origins:http://localhost:5173,http://localhost:3000,http://localhost:80}")
    private String[] allowedOrigins;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(patientResolver);
        resolvers.add(doctorResolver);
        resolvers.add(adminResolver);
        resolvers.add(userResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
