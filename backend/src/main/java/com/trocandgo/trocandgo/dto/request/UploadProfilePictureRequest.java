package com.trocandgo.trocandgo.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

    public class UploadProfilePictureRequest {
        @NotNull(message = "Image file is required.")
        private MultipartFile image;

        public MultipartFile getImage() {
            return image;
        }

        public void setImage(MultipartFile image) {
            this.image = image;
        }
}
