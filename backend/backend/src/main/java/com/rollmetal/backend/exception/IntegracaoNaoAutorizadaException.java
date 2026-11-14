package com.rollmetal.backend.exception;

public class IntegracaoNaoAutorizadaException extends RuntimeException {
    public IntegracaoNaoAutorizadaException() {
        super("Token de integração ausente ou inválido.");
    }
}
