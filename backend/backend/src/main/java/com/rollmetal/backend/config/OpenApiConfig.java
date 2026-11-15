package com.rollmetal.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "RollsMetal Backend API",
        version = "0.0.1-SNAPSHOT",
        description = "API REST para esteiras industriais, sensores e leituras encaminhadas pelo Node-RED."))
public class OpenApiConfig {
}
