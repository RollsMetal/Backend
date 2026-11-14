package com.rollmetal.backend.controller;

import com.rollmetal.backend.dto.sensor.SensorResponseDTO;
import com.rollmetal.backend.service.SensorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/esteiras/{numeroSerial}/sensores")
public class SensorController {
    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @GetMapping
    public List<SensorResponseDTO> listar(@PathVariable String numeroSerial) {
        return sensorService.listarPorEsteira(numeroSerial);
    }
}
