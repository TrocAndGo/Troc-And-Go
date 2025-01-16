package com.trocandgo.trocandgo.exception;

import org.springframework.http.HttpStatus;

public class InvalidServiceCategoryException extends RestApiException {
    public InvalidServiceCategoryException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
