package com.immiauto.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Immigration Automation API")
                        .description("AI-assisted intake and document follow-up for Canadian immigration consultants")
                        .version("0.0.1"));
    }
}
