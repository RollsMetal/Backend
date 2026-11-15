package com.rollmetal.backend.dto.esteira;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record EsteiraRequestDTO(
        @Schema(description = "Número serial único da esteira.", example = "RM-001",
                maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 100) String numeroSerial
) {
}
