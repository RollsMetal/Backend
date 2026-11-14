package com.rollmetal.backend.controller;

import com.rollmetal.backend.dto.esteira.EsteiraRequestDTO;
import com.rollmetal.backend.dto.esteira.EsteiraResponseDTO;
import com.rollmetal.backend.service.EsteiraService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/esteiras")
public class EsteiraController {
    private final EsteiraService esteiraService;

    public EsteiraController(EsteiraService esteiraService) {
        this.esteiraService = esteiraService;
    }

    @PostMapping
    public ResponseEntity<EsteiraResponseDTO> cadastrar(@Valid @RequestBody EsteiraRequestDTO request) {
        EsteiraResponseDTO resposta = esteiraService.cadastrar(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{numeroSerial}").buildAndExpand(resposta.numeroSerial()).toUri()).body(resposta);
    }

    @GetMapping("/{numeroSerial}")
    public EsteiraResponseDTO buscar(@PathVariable String numeroSerial) {
        return esteiraService.buscar(numeroSerial);
    }
}
