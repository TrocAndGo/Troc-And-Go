package com.trocandgo.trocandgo.exception;

import org.springframework.http.HttpStatus;

public class SelfReviewException extends RestApiException {
    public SelfReviewException() {
        super("A user cannot review its own service.");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
