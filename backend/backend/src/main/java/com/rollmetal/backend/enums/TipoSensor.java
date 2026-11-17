package com.rollmetal.backend.enums;

public enum TipoSensor {
    FOTOELETRICO,
    CAPACITIVO,
    INDUTIVO,

    // Valores legados mantidos para compatibilidade com registros já persistidos.
    SENSOR_CONDUTIVO,
    SENSOR_CAPACITIVO,
    SEM_SENSOR,
    SENSOR_CAPACITIVO_CONDUTIVO

}
