package com.trocandgo.trocandgo.dto.response;

public class UploadProfilePictureResponse {
    private String status;
    private String message;

    public UploadProfilePictureResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
