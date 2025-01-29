package com.trocandgo.trocandgo.exception;

import org.springframework.http.HttpStatus;

public class FavoriteDoesntExistException extends RestApiException {
    public FavoriteDoesntExistException() {
        super("Service is not in favorites.");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
