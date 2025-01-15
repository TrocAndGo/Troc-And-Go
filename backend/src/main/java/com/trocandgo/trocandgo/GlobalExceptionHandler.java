package com.trocandgo.trocandgo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.trocandgo.trocandgo.dto.response.RestApiExceptionResponse;
import com.trocandgo.trocandgo.exception.RestApiException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = RestApiException.class)
    public ResponseEntity<RestApiExceptionResponse> handleRestApiException(RestApiException e) {
        var title = e.getHttpStatus().getReasonPhrase();
        var status = e.getHttpStatus().value();
        var detail = e.getMessage();
        var instance = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getPath();
        var type = e.getClass().getSimpleName();
        var response = new RestApiExceptionResponse(title, status, detail, instance, type);

        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }
}
