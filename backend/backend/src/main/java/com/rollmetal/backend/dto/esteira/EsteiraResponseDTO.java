package com.rollmetal.backend.dto.esteira;

import io.swagger.v3.oas.annotations.media.Schema;

public record EsteiraResponseDTO(
        @Schema(description = "Identificador da esteira.", example = "1") Long id,
        @Schema(description = "Número serial da esteira.", example = "RM-001") String numeroSerial
) {
}
