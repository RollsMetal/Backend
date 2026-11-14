package com.rollmetal.backend.controller;

import com.rollmetal.backend.dto.leitura.SensorLeiturasPageResponseDTO;
import com.rollmetal.backend.service.SensorLeituraService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/esteiras/{numeroSerial}/leituras")
public class SensorLeituraController {
    private final SensorLeituraService sensorLeituraService;

    public SensorLeituraController(SensorLeituraService sensorLeituraService) {
        this.sensorLeituraService = sensorLeituraService;
    }

    @GetMapping
    public SensorLeiturasPageResponseDTO listar(
            @PathVariable String numeroSerial,
            @RequestParam(defaultValue = "0") @Min(0) int pagina,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int tamanho) {
        return sensorLeituraService.listarPorEsteira(numeroSerial, pagina, tamanho);
    }
}
