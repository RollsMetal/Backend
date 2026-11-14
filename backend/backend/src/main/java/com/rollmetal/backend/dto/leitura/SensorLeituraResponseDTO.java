package com.rollmetal.backend.dto.leitura;

import com.rollmetal.backend.enums.TipoMaterial;
import com.rollmetal.backend.enums.TipoSensor;

import java.time.OffsetDateTime;

public record SensorLeituraResponseDTO(
        Long id,
        Long sensorId,
        Short porta,
        String modelo,
        TipoSensor tipoSensor,
        String hexadecimal,
        TipoMaterial tipoMaterial,
        OffsetDateTime data
) {
}
