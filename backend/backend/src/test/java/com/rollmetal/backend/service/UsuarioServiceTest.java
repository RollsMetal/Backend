package com.rollmetal.backend.service;

import com.rollmetal.backend.database.model.UsuarioEntity;
import com.rollmetal.backend.database.repository.UsuarioRepository;
import com.rollmetal.backend.dto.usuario.UsuarioRequestDTO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsuarioServiceTest {
    private final UsuarioRepository repository = mock(UsuarioRepository.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final UsuarioService service = new UsuarioService(repository, encoder);

    @Test
    void persisteHashERespostaNaoIncluiSenha() {
        when(repository.existsByEmail("operador@rollmetal.com")).thenReturn(false);
        when(encoder.encode("SenhaSegura123")).thenReturn("$2a$12$hash");
        when(repository.save(any())).thenAnswer(invocation -> {
            UsuarioEntity entity = invocation.getArgument(0);
            entity.setId(9L);
            return entity;
        });

        var resposta = service.cadastrar(new UsuarioRequestDTO(
                "Operador", " Operador@RollMetal.com ", "SenhaSegura123"));

        assertEquals("operador@rollmetal.com", resposta.email());
        assertFalse(resposta.toString().contains("SenhaSegura123"));
        verify(encoder).encode("SenhaSegura123");
        ArgumentCaptor<UsuarioEntity> usuarioSalvo = ArgumentCaptor.forClass(UsuarioEntity.class);
        verify(repository).save(usuarioSalvo.capture());
        assertEquals("$2a$12$hash", usuarioSalvo.getValue().getSenha());
    }
}
