package com.devops_labs.userService.core.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

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
public class OpenApiConfig {
}
