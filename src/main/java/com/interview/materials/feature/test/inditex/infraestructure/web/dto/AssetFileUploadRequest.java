package com.interview.materials.feature.test.inditex.infraestructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record AssetFileUploadRequest(
        @NotBlank(message = "filename cannot be blank")
        String filename,

        @NotBlank(message = "encodedFile cannot be blank")
        String encodedFile,

        @NotBlank(message = "contentType cannot be blank")
        String contentType
) {
}