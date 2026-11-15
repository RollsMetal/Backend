package com.rollmetal.backend.service;

import com.rollmetal.backend.database.model.EsteiraEntity;
import com.rollmetal.backend.database.repository.EsteiraRepository;
import com.rollmetal.backend.dto.esteira.EsteiraRequestDTO;
import com.rollmetal.backend.exception.ConflitoDeConfiguracaoException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EsteiraServiceTest {
    private final EsteiraRepository repository = mock(EsteiraRepository.class);
    private final EsteiraService service = new EsteiraService(repository);

    @Test
    void cadastraSerialNormalizado() {
        when(repository.existsByNumeroSerial("RM-01")).thenReturn(false);
        when(repository.save(any())).thenAnswer(invocation -> {
            EsteiraEntity esteira = invocation.getArgument(0);
            return EsteiraEntity.builder().idEsteira(1L).numeroSerial(esteira.getNumeroSerial()).build();
        });

        var response = service.cadastrar(new EsteiraRequestDTO(" RM-01 "));

        assertEquals(1L, response.id());
        assertEquals("RM-01", response.numeroSerial());
        ArgumentCaptor<EsteiraEntity> saved = ArgumentCaptor.forClass(EsteiraEntity.class);
        verify(repository).save(saved.capture());
        assertEquals("RM-01", saved.getValue().getNumeroSerial());
    }

    @Test
    void rejeitaSerialDuplicado() {
        when(repository.existsByNumeroSerial("RM-01")).thenReturn(true);

        assertThrows(ConflitoDeConfiguracaoException.class,
                () -> service.cadastrar(new EsteiraRequestDTO("RM-01")));
    }

    @Test
    void listaEsteirasEmOrdemDoRepository() {
        when(repository.findAllByOrderByNumeroSerialAsc()).thenReturn(List.of(
                EsteiraEntity.builder().idEsteira(1L).numeroSerial("RM-01").build(),
                EsteiraEntity.builder().idEsteira(2L).numeroSerial("RM-02").build()));

        var response = service.listar();

        assertEquals(List.of("RM-01", "RM-02"), response.stream().map(item -> item.numeroSerial()).toList());
    }
}
