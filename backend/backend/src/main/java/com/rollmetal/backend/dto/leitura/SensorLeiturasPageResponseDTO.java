package com.rollmetal.backend.dto.leitura;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

public record SensorLeiturasPageResponseDTO(
        @Schema(description = "Leituras nesta página.") List<SensorLeituraResponseDTO> content,
        @Schema(description = "Índice da página, começando em zero.", example = "0") int page,
        @Schema(description = "Tamanho solicitado para a página.", example = "20") int size,
        @Schema(description = "Total de registros após aplicar os filtros.", example = "125") long totalElements,
        @Schema(description = "Quantidade total de páginas.", example = "7") int totalPages
) {
}
