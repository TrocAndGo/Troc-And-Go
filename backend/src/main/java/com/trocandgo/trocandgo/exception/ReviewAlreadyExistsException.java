package com.trocandgo.trocandgo.exception;

import org.springframework.http.HttpStatus;

public class ReviewAlreadyExistsException extends RestApiException {
    public ReviewAlreadyExistsException() {
        super("A review from this user already exists on this service.");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}
