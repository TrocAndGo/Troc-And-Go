package com.trocandgo.trocandgo.dto.request;

import jakarta.validation.constraints.Pattern;

public class ImagePath {

    @Pattern(regexp = "^[a-zA-Z0-9/_\\-]+\\.enc$",
             message = "Le chemin spécifié est invalide.")

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
