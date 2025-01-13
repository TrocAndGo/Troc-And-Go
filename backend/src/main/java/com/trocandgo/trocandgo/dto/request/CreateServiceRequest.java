package com.trocandgo.trocandgo.dto.request;

import com.trocandgo.trocandgo.entity.enums.ServiceStatusTitle;
import com.trocandgo.trocandgo.entity.enums.ServiceTypeTitle;

import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

@Value
public class CreateServiceRequest {
    @NotEmpty
    private String title;
    private String description;
    private ServiceTypeTitle type;
    private ServiceStatusTitle status = ServiceStatusTitle.OPENED;
    private long categoryId;
    private boolean useCreatorAdress = true;
}
