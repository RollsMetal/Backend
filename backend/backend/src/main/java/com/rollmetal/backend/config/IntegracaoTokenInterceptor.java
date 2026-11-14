package com.rollmetal.backend.config;

import com.rollmetal.backend.exception.IntegracaoNaoAutorizadaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class IntegracaoTokenInterceptor implements HandlerInterceptor {
    public static final String HEADER_NAME = "X-Integration-Token";

    private final byte[] tokenConfigurado;

    public IntegracaoTokenInterceptor(@Value("${rollmetal.integracao.token}") String token) {
        if (!StringUtils.hasText(token) || token.length() < 32) {
            throw new IllegalStateException("INTEGRATION_TOKEN deve possuir ao menos 32 caracteres.");
        }
        this.tokenConfigurado = token.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String recebido = request.getHeader(HEADER_NAME);
        byte[] tokenRecebido = recebido == null ? new byte[0] : recebido.getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(tokenConfigurado, tokenRecebido)) {
            throw new IntegracaoNaoAutorizadaException();
        }
        return true;
    }
}
