package com.trocandgo.trocandgo.exception;

import org.springframework.http.HttpStatus;

public class FavoriteAlreadyExistsException extends RestApiException {
    public FavoriteAlreadyExistsException() {
        super("Service is already in favorites.");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
