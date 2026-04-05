package com.bobridze5.TeleMed_backend.core.config;

import com.bobridze5.TeleMed_backend.api.dto.ApiErrorResponse;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentDoctor;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(info = @Info(
        title = "TeleMed API",
        description = "Backend для PWA приложения для пациентов с сахарным диабетом",
        version = "1.0.0",
        contact = @Contact(
                name = "Alsaev Dmitry",
                email = "dima101203@gmail.com",
                url = "https://github.com/bobridze5"
        )
))
@Configuration
public class OpenApiConfig {

    static {
        SpringDocUtils.getConfig().addAnnotationsToIgnore(
                CurrentPatient.class,
                CurrentDoctor.class
        );
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().components(new Components()
                .addSchemas(ApiErrorResponse.class.getSimpleName(),
                        new Schema<ApiErrorResponse>()
                                .description("Стандартный ответ с описанием ошибки")
                                .addProperty("timestamp", new Schema<>().type("string").format("date-time"))
                                .addProperty("status", new Schema<>().type("integer").example(404))
                                .addProperty("error", new Schema<>().type("string").example("Not Found"))
                                .addProperty("message", new Schema<>().type("string"))
                )
        );
    }

    @Bean
    public OperationCustomizer errorResponseCustomizer() {
        return (operation, handlerMethod) -> {
            ApiResponses responses = operation.getResponses();
            if (responses == null) return operation;

            responses.forEach((code, response) -> {
                if (isErrorCode(code) && response.getContent() == null) {
                    response.content(errorContent());
                }
            });

            return operation;
        };
    }

    private boolean isErrorCode(String code) {
        return code.startsWith("4") || code.startsWith("5");
    }

    private Content errorContent() {
        Schema<?> schema = new Schema<>().$ref(
                "#/components/schemas/" + ApiErrorResponse.class.getSimpleName()
        );
        return new Content().addMediaType(
                org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                new MediaType().schema(schema)
        );
    }
}
