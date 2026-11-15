package com.rollmetal.backend.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record UsuarioRequestDTO(
        @Schema(description = "Nome de exibição.", example = "Operador", maxLength = 150,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 150) String nome,
        @Schema(description = "E-mail único do usuário.", example = "operador@rollsmetal.com",
                format = "email", maxLength = 150, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Email @Size(max = 150) String email,
        @Schema(description = "Senha recebida para armazenamento BCrypt; não é usada para autenticação nesta versão.",
                example = "SenhaSegura123", minLength = 12, maxLength = 72, writeOnly = true,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(min = 12, max = 72) String senha
) {
}
