package com.rollmetal.backend.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.Map;

public record ApiErrorResponse(
        @Schema(description = "Instante UTC do erro.", format = "date-time")
        OffsetDateTime timestamp,
        @Schema(description = "Código HTTP.", example = "400")
        int status,
        @Schema(description = "Descrição curta do status.", example = "Bad Request")
        String error,
        @Schema(description = "Mensagem legível do erro.", example = "Um ou mais campos são inválidos.")
        String message,
        @Schema(description = "Caminho da requisição.", example = "/api/esteiras")
        String path,
        @Schema(description = "Erros de validação por campo.")
        Map<String, String> validationErrors
) {
}
