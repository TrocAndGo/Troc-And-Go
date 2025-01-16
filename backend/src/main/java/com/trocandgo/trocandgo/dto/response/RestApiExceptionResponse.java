package com.trocandgo.trocandgo.dto.response;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class RestApiExceptionResponse {
    private final String title;
    private final int status;
    private final String detail;
    private final String instance;
    private final String name;
}
