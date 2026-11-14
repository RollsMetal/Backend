package com.rollmetal.backend.service;

import com.rollmetal.backend.enums.TipoMaterial;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClassificadorMaterialTest {
    private final ClassificadorMaterial classificador = new ClassificadorMaterial();

    @Test
    void classificaMetalPeloPrefixoUm() {
        assertEquals(TipoMaterial.METAL, classificador.classificar("1A"));
    }

    @Test
    void classificaPlasticoPeloPrefixoZeroSete() {
        assertEquals(TipoMaterial.PLASTICO, classificador.classificar("07FF"));
    }

    @Test
    void classificaAusenciaDeMaterialPeloPrefixoZeroDois() {
        assertEquals(TipoMaterial.SEM_MATERIAL, classificador.classificar("02ff"));
    }

    @Test
    void mantemOutrosValoresValidosComoDesconhecidos() {
        assertEquals(TipoMaterial.DESCONHECIDO, classificador.classificar("03FF"));
    }
}
