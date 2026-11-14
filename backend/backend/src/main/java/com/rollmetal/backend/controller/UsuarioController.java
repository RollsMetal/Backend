package com.rollmetal.backend.controller;

import com.rollmetal.backend.dto.usuario.UsuarioRequestDTO;
import com.rollmetal.backend.dto.usuario.UsuarioResponseDTO;
import com.rollmetal.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO cadastrar(@Valid @RequestBody UsuarioRequestDTO request) {
        return usuarioService.cadastrar(request);
    }
}
