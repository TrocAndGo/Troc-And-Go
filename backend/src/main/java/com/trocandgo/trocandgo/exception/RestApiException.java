package com.trocandgo.trocandgo.exception;

import org.springframework.http.HttpStatus;

public abstract class RestApiException extends RuntimeException {
    public RestApiException(String message) {
        super(message);
    }

    public abstract HttpStatus getHttpStatus();
}
