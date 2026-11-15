package com.rollmetal.backend.controller;

import com.rollmetal.backend.dto.sensor.SensorResponseDTO;
import com.rollmetal.backend.service.SensorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/esteiras/{numeroSerial}/sensores")
@RequiredArgsConstructor
@Tag(name = "Sensores", description = "Consulta sensores associados a uma esteira.")
public class SensorController {
    private final SensorService sensorService;

    @GetMapping
    @Operation(summary = "Lista os sensores de uma esteira")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sensores retornados.", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = SensorResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Esteira não encontrada.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class)))
    })
    public List<SensorResponseDTO> listar(
            @Parameter(description = "Número serial cadastrado para a esteira.", example = "RM-001")
            @PathVariable String numeroSerial) {
        return sensorService.listarPorEsteira(numeroSerial);
    }
}
