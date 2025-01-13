package com.trocandgo.trocandgo.exception;

import org.springframework.http.HttpStatus;

public class FavoriteOwnServiceException extends RestApiException {

    public FavoriteOwnServiceException() {
        super("Favoriting a service created by self is not allowed");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

}
