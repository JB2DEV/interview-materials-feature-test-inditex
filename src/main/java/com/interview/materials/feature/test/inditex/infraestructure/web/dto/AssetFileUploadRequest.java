package com.interview.materials.feature.test.inditex.infraestructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AssetFileUploadRequest(
        @NotBlank(message = "filename cannot be blank")
        String filename,

        @NotBlank(message = "encodedFile cannot be blank")
        @Pattern(
                regexp = "^[A-Za-z0-9+/]+={0,2}$",
                message = "Invalid base64 encoding"
        )
        String encodedFile,

        @NotBlank(message = "contentType cannot be blank")
        String contentType
) {
}