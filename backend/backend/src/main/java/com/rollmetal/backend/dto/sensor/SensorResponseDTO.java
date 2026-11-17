package com.rollmetal.backend.dto.sensor;

import com.rollmetal.backend.enums.TipoSensor;
import io.swagger.v3.oas.annotations.media.Schema;

public record SensorResponseDTO(
        @Schema(description = "Identificador do sensor.", example = "12") Long id,
        @Schema(description = "Número serial da esteira associada.", example = "RM-001") String numeroSerialEsteira,
        @Schema(description = "Porta física do sensor.", example = "1") Short porta,
        @Schema(description = "Modelo atual do sensor.", example = "O5D150") String modelo,
        @Schema(description = "Tipo identificado para o modelo.", example = "FOTOELETRICO") TipoSensor tipoSensor
) {
}
