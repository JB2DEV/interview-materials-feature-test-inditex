package com.interview.materials.feature.test.inditex.infraestructure.web.error;

import java.util.List;

public record ErrorResponse(
        int status,
        String error,
        String message,
        String timestamp,
        List<FieldError> invalidParams
) {
    public record FieldError(String field, String message) {
    }
}