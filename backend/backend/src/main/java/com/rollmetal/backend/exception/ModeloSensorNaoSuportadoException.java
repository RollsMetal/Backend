package com.rollmetal.backend.exception;

public class ModeloSensorNaoSuportadoException extends RuntimeException {
    public ModeloSensorNaoSuportadoException(String modelo) {
        super("Modelo de sensor não suportado: " + modelo);
    }
}
