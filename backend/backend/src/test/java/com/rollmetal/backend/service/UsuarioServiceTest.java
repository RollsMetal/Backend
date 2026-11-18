package com.rollmetal.backend.service;

import com.rollmetal.backend.database.model.UsuarioEntity;
import com.rollmetal.backend.database.repository.UsuarioRepository;
import com.rollmetal.backend.dto.usuario.UsuarioLoginRequestDTO;
import com.rollmetal.backend.dto.usuario.UsuarioRequestDTO;
import com.rollmetal.backend.exception.CredenciaisInvalidasException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

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
            return UsuarioEntity.builder()
                    .id(9L)
                    .nome(entity.getNome())
                    .email(entity.getEmail())
                    .senha(entity.getSenha())
                    .build();
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

    @Test
    void loginNormalizaEmailComparaSenhaBCryptERetornaSomenteDadosPublicos() {
        UsuarioEntity usuario = UsuarioEntity.builder()
                .id(9L)
                .nome("Operador")
                .email("operador@rollmetal.com")
                .senha("$2a$12$hash")
                .build();
        when(repository.findByEmail("operador@rollmetal.com")).thenReturn(Optional.of(usuario));
        when(encoder.matches("SenhaSegura123", "$2a$12$hash")).thenReturn(true);

        var resposta = service.login(new UsuarioLoginRequestDTO(
                " Operador@RollMetal.com ", "SenhaSegura123"));

        assertEquals(9L, resposta.id());
        assertEquals("Operador", resposta.nome());
        assertEquals("operador@rollmetal.com", resposta.email());
        assertFalse(resposta.toString().contains("hash"));
        assertFalse(resposta.toString().contains("SenhaSegura123"));
        verify(repository).findByEmail("operador@rollmetal.com");
        verify(encoder).matches("SenhaSegura123", "$2a$12$hash");
    }

    @Test
    void loginRejeitaSenhaIncorretaSemDetalharQualCredencialFalhou() {
        UsuarioEntity usuario = UsuarioEntity.builder()
                .id(9L)
                .nome("Operador")
                .email("operador@rollmetal.com")
                .senha("$2a$12$hash")
                .build();
        when(repository.findByEmail("operador@rollmetal.com")).thenReturn(Optional.of(usuario));
        when(encoder.matches("senha-incorreta", "$2a$12$hash")).thenReturn(false);

        assertThrows(CredenciaisInvalidasException.class, () -> service.login(
                new UsuarioLoginRequestDTO("operador@rollmetal.com", "senha-incorreta")));
        verify(encoder).matches("senha-incorreta", "$2a$12$hash");
    }

    @Test
    void loginRejeitaEmailInexistente() {
        when(repository.findByEmail("inexistente@rollmetal.com")).thenReturn(Optional.empty());

        assertThrows(CredenciaisInvalidasException.class, () -> service.login(
                new UsuarioLoginRequestDTO("inexistente@rollmetal.com", "SenhaSegura123")));
        verify(repository).findByEmail("inexistente@rollmetal.com");
        verifyNoInteractions(encoder);
    }

    @Test
    void loginRejeitaSenhaQueExcedeLimiteTecnicoDoBCryptEmBytes() {
        String senhaAcimaDoLimiteEmUtf8 = "ç".repeat(37);

        assertThrows(CredenciaisInvalidasException.class, () -> service.login(
                new UsuarioLoginRequestDTO("operador@rollmetal.com", senhaAcimaDoLimiteEmUtf8)));
        verifyNoInteractions(repository, encoder);
    }
}
