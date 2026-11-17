package com.rollmetal.backend.exception;

public class ConflitoDeConfiguracaoException extends RuntimeException {
    public ConflitoDeConfiguracaoException(String mensagem) {
        super(mensagem);
    }
}
