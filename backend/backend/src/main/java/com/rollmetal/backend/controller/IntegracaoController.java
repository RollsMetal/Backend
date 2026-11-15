package com.rollmetal.backend.controller;

import com.rollmetal.backend.dto.leitura.SensorLeituraRequestDTO;
import com.rollmetal.backend.dto.leitura.SensorLeituraResponseDTO;
import com.rollmetal.backend.service.SensorLeituraService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/integracao/esteiras/{numeroSerial}/leituras")
@RequiredArgsConstructor
@Tag(name = "Integração industrial", description = "Recebe leituras encaminhadas pelo Node-RED.")
public class IntegracaoController {
    private final SensorLeituraService sensorLeituraService;

    @PostMapping
    @Operation(summary = "Registra um lote de leituras",
            description = "Valida o lote inteiro e persiste sensores e histórico em uma transação.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lote registrado.", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = SensorLeituraResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Lote vazio, inválido ou com portas duplicadas.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Esteira não encontrada.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflito de integridade no banco.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Modelo de sensor não suportado.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno.", content = @Content(
                    schema = @Schema(implementation = com.rollmetal.backend.exception.ApiErrorResponse.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    public List<SensorLeituraResponseDTO> registrar(
            @Parameter(description = "Número serial da esteira.", example = "RM-001")
            @PathVariable String numeroSerial,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Amostras enviadas pelo Node-RED. Cada porta deve ocorrer uma vez no lote.",
                    required = true,
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = SensorLeituraRequestDTO.class)),
                            examples = @ExampleObject(value = "[{\"porta\":1,\"modelo\":\"O5D150\",\"hexadecimal\":\"1A\"}]")))
            @RequestBody @NotEmpty List<@NotNull @Valid SensorLeituraRequestDTO> leituras) {
        return sensorLeituraService.registrarLeituras(numeroSerial, leituras);
    }
}
