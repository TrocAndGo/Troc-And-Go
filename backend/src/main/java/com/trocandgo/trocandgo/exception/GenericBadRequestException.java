package com.trocandgo.trocandgo.exception;

import org.springframework.http.HttpStatus;

public class GenericBadRequestException extends RestApiException {
    public GenericBadRequestException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }


}
