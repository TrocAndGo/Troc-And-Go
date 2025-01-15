package com.trocandgo.trocandgo.dto.request;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

@Value
public class CreateReviewRequest {
    @NotEmpty
    private String comment;

    @NonNull
    @DecimalMin("0")
    @DecimalMax("5")
    private Integer rating;
}
