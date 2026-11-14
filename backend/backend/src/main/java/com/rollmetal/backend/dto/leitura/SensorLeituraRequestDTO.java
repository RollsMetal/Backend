package com.rollmetal.backend.dto.leitura;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Amostra recebida do Node-RED/Python; o hexadecimal é preservado sem normalização. */
public record SensorLeituraRequestDTO(
        @NotNull @Positive Short porta,
        @NotBlank @Size(max = 50) String modelo,
        @NotBlank @Size(max = 64) @Pattern(regexp = "(?i)[0-9a-f]+") String hexadecimal
) {
}
