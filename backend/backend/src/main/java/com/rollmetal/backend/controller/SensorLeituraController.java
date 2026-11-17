package com.rollmetal.backend.controller;

import com.rollmetal.backend.dto.leitura.SensorLeiturasPageResponseDTO;
import com.rollmetal.backend.enums.TipoMaterial;
import com.rollmetal.backend.service.SensorLeituraService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
@Validated
@RestController
@RequestMapping("/api/esteiras/{numeroSerial}/leituras")
@RequiredArgsConstructor
@Tag(name = "Leituras", description = "Consulta paginada e filtrada do histórico de leituras.")
public class SensorLeituraController {
    private final SensorLeituraService sensorLeituraService;

    @GetMapping
    @Operation(summary = "Consulta o histórico de leituras de uma esteira",
            description = "Filtros opcionais podem limitar por sensor, porta, material e intervalo ISO-8601.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de leituras retornada.", content = @Content(
                    schema = @Schema(implementation = SensorLeiturasPageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetro inválido.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Esteira não encontrada.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class)))
    })
    public SensorLeiturasPageResponseDTO listar(
            @Parameter(description = "Número serial da esteira.", example = "RM-001")
            @PathVariable String numeroSerial,
            @Parameter(description = "Índice da página, começando em zero.", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int pagina,
            @Parameter(description = "Quantidade por página (1–100).", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int tamanho,
            @Parameter(description = "Filtra pelo ID do sensor.", example = "12")
            @RequestParam(required = false) @Positive Long sensorId,
            @Parameter(description = "Filtra pelo número da porta do sensor.", example = "1")
            @RequestParam(required = false) @Positive Short porta,
            @Parameter(description = "Filtra pela classificação do material.", example = "METAL")
            @RequestParam(required = false) TipoMaterial tipoMaterial,
            @Parameter(description = "Limite inferior inclusivo em ISO-8601.", example = "2026-11-14T00:00:00Z")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime dataInicio,
            @Parameter(description = "Limite superior inclusivo em ISO-8601.", example = "2026-11-15T00:00:00Z")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime dataFim) {
        return sensorLeituraService.listarPorEsteira(
                numeroSerial, pagina, tamanho, sensorId, porta, tipoMaterial, dataInicio, dataFim);
    }
}
