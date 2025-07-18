package com.interview.materials.feature.test.inditex.infraestructure.web.dto;

public record AssetFileUploadRequest(
        String filename,
        String encodedFile,
        String contentType
) {}