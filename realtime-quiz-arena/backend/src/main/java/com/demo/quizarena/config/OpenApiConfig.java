package com.demo.quizarena.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("Realtime Quiz Arena API (Demo)")
                .version("0.1.0")
                .description("REST APIs for quiz management + rooms/sessions. Real-time gameplay uses WebSocket/STOMP."));
    }
}
