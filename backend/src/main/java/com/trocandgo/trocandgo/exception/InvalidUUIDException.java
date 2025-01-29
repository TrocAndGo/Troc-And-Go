package com.trocandgo.trocandgo.exception;

import org.springframework.http.HttpStatus;

public class InvalidUUIDException extends RestApiException {
    public InvalidUUIDException(String uuid) {
        super("Invalid UUID: " + uuid);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }}
