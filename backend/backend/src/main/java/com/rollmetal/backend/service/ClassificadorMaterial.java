package com.rollmetal.backend.service;

import com.rollmetal.backend.enums.TipoMaterial;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Objects;

/** Regras provisórias isoladas para classificar o prefixo do valor hexadecimal. */
@Component
public class ClassificadorMaterial {

    public TipoMaterial classificar(String hexadecimal) {
        Objects.requireNonNull(hexadecimal, "O valor hexadecimal é obrigatório");
        String valor = hexadecimal.toUpperCase(Locale.ROOT);

        if (valor.startsWith("07")) {
            return TipoMaterial.PLASTICO;
        }
        if (valor.startsWith("02")) {
            return TipoMaterial.SEM_MATERIAL;
        }
        if (valor.startsWith("1")) {
            return TipoMaterial.METAL;
        }
        return TipoMaterial.DESCONHECIDO;
    }
}
