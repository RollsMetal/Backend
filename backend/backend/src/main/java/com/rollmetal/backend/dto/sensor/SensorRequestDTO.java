package com.rollmetal.backend.dto.sensor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record SensorRequestDTO(
        @Schema(description = "Porta física do sensor na esteira.", example = "1",
                minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @Positive Short porta,
        @Schema(description = "Modelo confirmado no catálogo.", example = "O5D150",
                allowableValues = {"O5D150", "KI5309", "IF6123"}, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 50) String modelo
) {
}
