package com.rollmetal.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> recursoNaoEncontrado(
            RecursoNaoEncontradoException exception, HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(IntegracaoNaoAutorizadaException.class)
    public ResponseEntity<ApiErrorResponse> integracaoNaoAutorizada(
            IntegracaoNaoAutorizadaException exception, HttpServletRequest request) {
        return response(HttpStatus.UNAUTHORIZED, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(ModeloSensorNaoSuportadoException.class)
    public ResponseEntity<ApiErrorResponse> modeloNaoSuportado(
            ModeloSensorNaoSuportadoException exception, HttpServletRequest request) {
        return response(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(RequisicaoInvalidaException.class)
    public ResponseEntity<ApiErrorResponse> requisicaoInvalida(
            RequisicaoInvalidaException exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler({ConflitoDeConfiguracaoException.class, EmailJaCadastradoException.class,
            DataIntegrityViolationException.class})
    public ResponseEntity<ApiErrorResponse> conflito(RuntimeException exception, HttpServletRequest request) {
        String mensagem = exception instanceof DataIntegrityViolationException
                ? "A operação conflita com dados já cadastrados."
                : exception.getMessage();
        return response(HttpStatus.CONFLICT, mensagem, request, Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> argumentosInvalidos(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> erros = new LinkedHashMap<>();
        for (FieldError erro : exception.getBindingResult().getFieldErrors()) {
            erros.putIfAbsent(erro.getField(), erro.getDefaultMessage());
        }
        return response(HttpStatus.BAD_REQUEST, "Um ou mais campos são inválidos.", request, erros);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> violacaoDeConstraint(
            ConstraintViolationException exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "Um ou mais parâmetros são inválidos.", request, Map.of());
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorResponse> validacaoDeMetodo(
            HandlerMethodValidationException exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "Um ou mais parâmetros são inválidos.", request, Map.of());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> corpoInvalido(
            HttpMessageNotReadableException exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "O corpo da requisição está ausente ou malformado.", request, Map.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> erroInterno(Exception exception, HttpServletRequest request) {
        LOGGER.error("Erro não tratado ao processar {} {}", request.getMethod(), request.getRequestURI(), exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno ao processar a requisição.", request, Map.of());
    }

    private ResponseEntity<ApiErrorResponse> response(HttpStatus status, String message,
                                                       HttpServletRequest request,
                                                       Map<String, String> validationErrors) {
        ApiErrorResponse body = new ApiErrorResponse(
                OffsetDateTime.now(ZoneOffset.UTC), status.value(), status.getReasonPhrase(),
                message, request.getRequestURI(), validationErrors);
        return ResponseEntity.status(status).body(body);
    }
}
