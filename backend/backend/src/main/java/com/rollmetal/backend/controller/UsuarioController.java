package com.rollmetal.backend.controller;

import com.rollmetal.backend.dto.usuario.UsuarioRequestDTO;
import com.rollmetal.backend.dto.usuario.UsuarioLoginRequestDTO;
import com.rollmetal.backend.dto.usuario.UsuarioResponseDTO;
import com.rollmetal.backend.service.UsuarioService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Cadastro de usuários e validação de credenciais.")
public class UsuarioController {
    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Cadastra um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado.", content = @Content(
                    schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO cadastrar(@Valid @RequestBody UsuarioRequestDTO request) {
        return usuarioService.cadastrar(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Valida as credenciais de um usuário",
            description = "Confere o e-mail e a senha cadastrados com BCrypt e retorna os dados públicos do usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credenciais válidas.", content = @Content(
                    schema = @Schema(implementation = UsuarioResponseDTO.class),
                    examples = @ExampleObject(value = "{\"id\":1,\"nome\":\"Operador\",\"email\":\"operador@rollsmetal.com\"}"))),
            @ApiResponse(responseCode = "400", description = "Payload inválido.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "E-mail ou senha inválidos.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class),
                    examples = @ExampleObject(value = "{\"timestamp\":\"2026-11-17T21:00:00Z\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"E-mail ou senha inválidos.\",\"path\":\"/api/usuarios/login\",\"validationErrors\":{}}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class)))
    })
    public UsuarioResponseDTO login(@Valid @RequestBody UsuarioLoginRequestDTO request) {
        return usuarioService.login(request);
    }
}
