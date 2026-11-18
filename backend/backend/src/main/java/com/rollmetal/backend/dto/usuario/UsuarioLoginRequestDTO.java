package com.rollmetal.backend.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioLoginRequestDTO(
        @Schema(description = "E-mail cadastrado do usuário.", example = "operador@rollsmetal.com",
                format = "email", maxLength = 150, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Email @Size(max = 150) String email,
        @Schema(description = "Senha cadastrada, validada com BCrypt. O limite técnico é de 72 bytes UTF-8.",
                example = "SenhaSegura123", maxLength = 72, writeOnly = true,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 72) String senha
) {
    public UsuarioLoginRequestDTO {
        if (email != null) {
            email = email.strip();
        }
    }
}
