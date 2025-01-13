package com.trocandgo.trocandgo.exception;

import org.springframework.http.HttpStatus;

public class InvalidPageableException extends RestApiException {
    public InvalidPageableException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }}
