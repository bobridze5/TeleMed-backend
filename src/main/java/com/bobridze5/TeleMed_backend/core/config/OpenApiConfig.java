package com.bobridze5.TeleMed_backend.core.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(info = @Info(
        title = "API",
        description = "API TEST",
        version = "1.0.0",
        contact = @Contact(
                name = "Alsaev Dmitry",
                email = "dima101203@gmail.com",
                url = "https://github.com/bobridze5"
        )
)
)
@Configuration
public class OpenApiConfig {
    static {
        // TODO: исправить видимость пациента в swagger-ui
        SpringDocUtils.getConfig().addAnnotationsToIgnore(
                com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient.class
        );
    }
}
