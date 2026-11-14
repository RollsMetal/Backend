package com.rollmetal.backend.dto.leitura;

import java.util.List;

public record SensorLeiturasPageResponseDTO(
        List<SensorLeituraResponseDTO> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
