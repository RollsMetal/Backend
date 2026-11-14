package com.rollmetal.backend.dto.sensor;

import com.rollmetal.backend.enums.TipoSensor;

public record SensorResponseDTO(
        Long id,
        String numeroSerialEsteira,
        Short porta,
        String modelo,
        TipoSensor tipoSensor
) {
}
