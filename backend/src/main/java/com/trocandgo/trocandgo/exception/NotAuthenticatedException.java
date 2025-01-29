package com.trocandgo.trocandgo.exception;

import org.springframework.http.HttpStatus;

public class NotAuthenticatedException extends RestApiException {

    public NotAuthenticatedException() {
        super("Authentication is required.");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
