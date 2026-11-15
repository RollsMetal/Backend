package com.rollmetal.backend.service;

import com.rollmetal.backend.database.model.EsteiraEntity;
import com.rollmetal.backend.database.model.SensorEntity;
import com.rollmetal.backend.database.repository.EsteiraRepository;
import com.rollmetal.backend.database.repository.SensorRepository;
import com.rollmetal.backend.enums.TipoSensor;
import com.rollmetal.backend.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SensorServiceTest {
    private final EsteiraRepository esteiras = mock(EsteiraRepository.class);
    private final SensorRepository sensores = mock(SensorRepository.class);
    private final SensorService service = new SensorService(esteiras, sensores);

    @Test
    void listaSensoresAssociadosAEsteira() {
        EsteiraEntity esteira = EsteiraEntity.builder().idEsteira(2L).numeroSerial("RM-02").build();
        SensorEntity sensor = SensorEntity.builder()
                .idSensor(7L)
                .esteira(esteira)
                .porta((short) 1)
                .modelo("O5D150")
                .tipoSensor(TipoSensor.FOTOELETRICO)
                .build();
        when(esteiras.findByNumeroSerial("RM-02")).thenReturn(Optional.of(esteira));
        when(sensores.findByEsteira_IdEsteiraOrderByPorta(2L)).thenReturn(List.of(sensor));

        var response = service.listarPorEsteira("RM-02");

        assertEquals(1, response.size());
        assertEquals(7L, response.getFirst().id());
        assertEquals("RM-02", response.getFirst().numeroSerialEsteira());
        assertEquals(TipoSensor.FOTOELETRICO, response.getFirst().tipoSensor());
        verify(sensores).findByEsteira_IdEsteiraOrderByPorta(2L);
    }

    @Test
    void rejeitaEsteiraInexistente() {
        when(esteiras.findByNumeroSerial("RM-404")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.listarPorEsteira("RM-404"));
    }
}
