package com.rollmetal.backend.service;

import com.rollmetal.backend.database.model.UsuarioEntity;
import com.rollmetal.backend.database.repository.UsuarioRepository;
import com.rollmetal.backend.dto.usuario.UsuarioLoginRequestDTO;
import com.rollmetal.backend.dto.usuario.UsuarioRequestDTO;
import com.rollmetal.backend.dto.usuario.UsuarioResponseDTO;
import com.rollmetal.backend.exception.CredenciaisInvalidasException;
import com.rollmetal.backend.exception.EmailJaCadastradoException;
import com.rollmetal.backend.exception.RequisicaoInvalidaException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO request) {
        if (request.senha().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new RequisicaoInvalidaException("A senha excede o limite de bytes aceito pelo BCrypt.");
        }
        String email = request.email().strip().toLowerCase(Locale.ROOT);
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailJaCadastradoException(email);
        }
        UsuarioEntity usuario = usuarioRepository.save(UsuarioEntity.builder()
                .nome(request.nome().strip())
                .email(email)
                .senha(passwordEncoder.encode(request.senha()))
                .build());
        return new UsuarioResponseDTO(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO login(UsuarioLoginRequestDTO request) {
        if (request.senha().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new CredenciaisInvalidasException();
        }

        String email = request.email().strip().toLowerCase(Locale.ROOT);
        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException();
        }

        return new UsuarioResponseDTO(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
