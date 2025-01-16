package com.trocandgo.trocandgo.exception;

import org.springframework.http.HttpStatus;

public class ServiceNotFoundException extends RestApiException {
    public ServiceNotFoundException(String serviceId) {
        super("No service found with id: " + serviceId);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
