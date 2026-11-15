package com.rollmetal.backend.controller;

import com.rollmetal.backend.dto.usuario.UsuarioRequestDTO;
import com.rollmetal.backend.dto.usuario.UsuarioResponseDTO;
import com.rollmetal.backend.service.UsuarioService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
@Tag(name = "Usuários", description = "Cadastro de usuários do sistema.")
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
}
