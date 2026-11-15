package com.rollmetal.backend.dto.leitura;

import com.rollmetal.backend.enums.TipoMaterial;
import com.rollmetal.backend.enums.TipoSensor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

public record SensorLeituraResponseDTO(
        @Schema(description = "Identificador da leitura.", example = "9001") Long id,
        @Schema(description = "Identificador do sensor associado.", example = "12") Long sensorId,
        @Schema(description = "Porta física do sensor.", example = "1") Short porta,
        @Schema(description = "Modelo recebido e preservado no histórico.", example = "O5D150") String modelo,
        @Schema(description = "Tipo associado ao modelo recebido.", example = "FOTOELETRICO") TipoSensor tipoSensor,
        @Schema(description = "Hexadecimal original, sem normalização.", example = "1A0F") String hexadecimal,
        @Schema(description = "Classificação provisória do material.", example = "METAL") TipoMaterial tipoMaterial,
        @Schema(description = "Instante UTC da leitura em ISO-8601.", example = "2026-11-14T18:30:00Z",
                format = "date-time") OffsetDateTime data
) {
}
