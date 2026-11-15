package com.rollmetal.backend.controller;

import com.rollmetal.backend.dto.esteira.EsteiraRequestDTO;
import com.rollmetal.backend.dto.esteira.EsteiraResponseDTO;
import com.rollmetal.backend.service.EsteiraService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
@RestController
@RequestMapping("/api/esteiras")
@RequiredArgsConstructor
@Tag(name = "Esteiras", description = "Cadastro e consulta de esteiras industriais.")
public class EsteiraController {
    private final EsteiraService esteiraService;

    @GetMapping
    @Operation(summary = "Lista as esteiras", description = "Retorna as esteiras ordenadas pelo número serial.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada.", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = EsteiraResponseDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Erro interno.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class)))
    })
    public List<EsteiraResponseDTO> listar() {
        return esteiraService.listar();
    }

    @PostMapping
    @Operation(summary = "Cadastra uma esteira")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Esteira cadastrada.", content = @Content(
                    schema = @Schema(implementation = EsteiraResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Número serial já cadastrado.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class)))
    })
    public ResponseEntity<EsteiraResponseDTO> cadastrar(@Valid @RequestBody EsteiraRequestDTO request) {
        EsteiraResponseDTO resposta = esteiraService.cadastrar(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{numeroSerial}").buildAndExpand(resposta.numeroSerial()).toUri()).body(resposta);
    }

    @GetMapping("/{numeroSerial}")
    @Operation(summary = "Consulta uma esteira pelo número serial")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Esteira encontrada.", content = @Content(
                    schema = @Schema(implementation = EsteiraResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Esteira não encontrada.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class)))
    })
    public EsteiraResponseDTO buscar(
            @Parameter(description = "Número serial cadastrado para a esteira.", example = "RM-001")
            @PathVariable String numeroSerial) {
        return esteiraService.buscar(numeroSerial);
    }
}
