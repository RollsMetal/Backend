package com.rollmetal.backend.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;

public record UsuarioResponseDTO(
        @Schema(description = "Identificador do usuário.", example = "3") Long id,
        @Schema(description = "Nome de exibição.", example = "Operador") String nome,
        @Schema(description = "E-mail cadastrado.", example = "operador@rollsmetal.com", format = "email") String email
) {
}
