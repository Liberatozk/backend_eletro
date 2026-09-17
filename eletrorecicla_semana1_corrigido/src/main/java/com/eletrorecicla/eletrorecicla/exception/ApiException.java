package com.eletrorecicla.eletrorecicla.exception;

import org.springframework.http.HttpStatus;

/** Exceção simples para devolver um status HTTP + mensagem legível ao invés de um 500 genérico. */
public class ApiException extends RuntimeException {
    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() { return status; }
}
