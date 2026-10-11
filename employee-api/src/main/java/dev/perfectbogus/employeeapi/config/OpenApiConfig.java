package dev.perfectbogus.employeeapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI employeeApiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Employee API")
                        .description("REST API for managing employees")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("perfectbogus")
                                .url("https://github.com/perfectbogus")));
    }
}
