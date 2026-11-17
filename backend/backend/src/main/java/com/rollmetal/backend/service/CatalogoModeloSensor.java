package com.rollmetal.backend.service;

import com.rollmetal.backend.enums.TipoSensor;
import com.rollmetal.backend.exception.ModeloSensorNaoSuportadoException;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

/** Catálogo fechado dos modelos confirmados para a integração atual. */
@Component
public class CatalogoModeloSensor {
    private static final Map<String, TipoSensor> MODELOS = Map.of(
            "O5D150", TipoSensor.FOTOELETRICO,
            "KI5309", TipoSensor.CAPACITIVO,
            "IF6123", TipoSensor.INDUTIVO
    );

    public TipoSensor identificar(String modelo) {
        if (modelo == null || modelo.isBlank()) {
            throw new ModeloSensorNaoSuportadoException(String.valueOf(modelo));
        }
        String chave = modelo.strip().toUpperCase(Locale.ROOT);
        TipoSensor tipo = MODELOS.get(chave);
        if (tipo == null) {
            throw new ModeloSensorNaoSuportadoException(modelo);
        }
        return tipo;
    }

    public String modeloCanonico(String modelo) {
        String chave = modelo.strip().toUpperCase(Locale.ROOT);
        identificar(chave);
        return chave;
    }
}
