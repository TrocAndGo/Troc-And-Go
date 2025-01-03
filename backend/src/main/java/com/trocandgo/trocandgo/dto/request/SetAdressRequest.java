package com.trocandgo.trocandgo.dto.request;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

@Value
public class SetAdressRequest {
    private long userId; //TODO: Only for debugging purposes until login is implemented
    @NotEmpty
    private String adress;
    @NotEmpty
    private String city;
    @NotEmpty
    private String zipCode;
    @NotEmpty
    private String department;
    @NotEmpty
    private String region;
    @NotEmpty
    private String country;

    private BigDecimal latitude;
    private BigDecimal longitude;
}
