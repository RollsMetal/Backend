package com.rollmetal.backend.exception;

public class EmailJaCadastradoException extends RuntimeException {
    public EmailJaCadastradoException(String email) {
        super("Já existe um usuário com o e-mail informado: " + email);
    }
}
