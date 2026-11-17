package com.rollmetal.backend.service;

import com.rollmetal.backend.enums.TipoSensor;
import com.rollmetal.backend.exception.ModeloSensorNaoSuportadoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CatalogoModeloSensorTest {
    private final CatalogoModeloSensor catalogo = new CatalogoModeloSensor();

    @Test
    void identificaSomenteModelosInformados() {
        assertEquals(TipoSensor.FOTOELETRICO, catalogo.identificar("O5D150"));
        assertEquals(TipoSensor.CAPACITIVO, catalogo.identificar("KI5309"));
        assertEquals(TipoSensor.INDUTIVO, catalogo.identificar("IF6123"));
    }

    @Test
    void rejeitaModeloDesconhecidoSemCriarClassificacao() {
        assertThrows(ModeloSensorNaoSuportadoException.class, () -> catalogo.identificar("OUTRO"));
    }
}
