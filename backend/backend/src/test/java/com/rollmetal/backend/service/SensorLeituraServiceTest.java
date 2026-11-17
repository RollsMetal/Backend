package com.rollmetal.backend.service;

import com.rollmetal.backend.database.model.EsteiraEntity;
import com.rollmetal.backend.database.repository.EsteiraRepository;
import com.rollmetal.backend.database.repository.SensorLeituraRepository;
import com.rollmetal.backend.database.repository.SensorRepository;
import com.rollmetal.backend.dto.leitura.SensorLeituraRequestDTO;
import com.rollmetal.backend.enums.TipoMaterial;
import com.rollmetal.backend.exception.ModeloSensorNaoSuportadoException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SensorLeituraServiceTest {
    private final EsteiraRepository esteiras = mock(EsteiraRepository.class);
    private final SensorRepository sensores = mock(SensorRepository.class);
    private final SensorLeituraRepository leituras = mock(SensorLeituraRepository.class);
    private final SensorLeituraService service = new SensorLeituraService(
            esteiras, sensores, leituras, new CatalogoModeloSensor(), new ClassificadorMaterial());

    @Test
    void preservaHexadecimalRecebidoEClassificaLeitura() {
        when(esteiras.findByNumeroSerial("RM-01"))
                .thenReturn(Optional.of(EsteiraEntity.builder().idEsteira(1L).numeroSerial("RM-01").build()));
        when(sensores.findByEsteira_IdEsteiraAndPorta(1L, (short) 1)).thenReturn(Optional.empty());
        when(sensores.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(leituras.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var resposta = service.registrarLeituras("RM-01",
                List.of(new SensorLeituraRequestDTO((short) 1, "O5D150", "1a0f")));

        assertEquals("1a0f", resposta.getFirst().hexadecimal());
        assertEquals(TipoMaterial.METAL, resposta.getFirst().tipoMaterial());
    }

    @Test
    void rejeitaModeloDesconhecidoAntesDeGravar() {
        when(esteiras.findByNumeroSerial("RM-01"))
                .thenReturn(Optional.of(EsteiraEntity.builder().idEsteira(1L).numeroSerial("RM-01").build()));

        assertThrows(ModeloSensorNaoSuportadoException.class, () -> service.registrarLeituras("RM-01",
                List.of(new SensorLeituraRequestDTO((short) 1, "OUTRO", "1a0f"))));
        verify(sensores, never()).save(any());
        verify(leituras, never()).save(any());
    }
}
