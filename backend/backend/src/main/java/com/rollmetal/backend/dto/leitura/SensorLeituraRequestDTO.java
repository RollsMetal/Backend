package com.rollmetal.backend.dto.leitura;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

/** Amostra recebida do Node-RED/Python; o hexadecimal é preservado sem normalização. */
public record SensorLeituraRequestDTO(
        @Schema(description = "Porta física do sensor na esteira.", example = "1",
                minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @Positive Short porta,
        @Schema(description = "Modelo reportado pelo IO-Link Master.", example = "O5D150",
                allowableValues = {"O5D150", "KI5309", "IF6123"}, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 50) String modelo,
        @Schema(description = "Valor hexadecimal original; 1 a 64 dígitos, sem prefixo 0x.",
                example = "1A0F", pattern = "(?i)[0-9a-f]+", minLength = 1, maxLength = 64,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 64) @Pattern(regexp = "(?i)[0-9a-f]+") String hexadecimal
) {
}
