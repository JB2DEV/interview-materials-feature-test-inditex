package com.interview.materials.feature.test.inditex.application.validation;

import com.interview.materials.feature.test.inditex.application.validation.error.InvalidBase64EncodedAssetException;
import com.interview.materials.feature.test.inditex.application.validation.error.InvalidDateRangeException;
import com.interview.materials.feature.test.inditex.application.validation.error.InvalidSortDirectionException;
import com.interview.materials.feature.test.inditex.application.validation.error.UnsupportedAssetContentTypeException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
public class AssetValidator {

    private static final List<String> ALLOWED_SORT_DIRECTIONS = List.of("ASC", "DESC");

    public void validateEncodedFile(String encodedFile) {
        if (!isValidBase64(encodedFile)) {
            throw new InvalidBase64EncodedAssetException("The encoded file is not valid base64.");
        }
    }

    public void validateContentType(String contentType) {
        if (!isSupportedContentType(contentType)) {
            throw new UnsupportedAssetContentTypeException("Unsupported content type: " + contentType);
        }
    }

    public void validateSortDirection(String sortDirection) {
        if (sortDirection != null && !ALLOWED_SORT_DIRECTIONS.contains(sortDirection.toUpperCase(Locale.ROOT))) {
            throw new InvalidSortDirectionException("Invalid sort direction: " + sortDirection);
        }
    }

    public void validateDateRange(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new InvalidDateRangeException("Start date must be before or equal to end date.");
        }
    }

    private boolean isValidBase64(String input) {
        try {
            Base64.getDecoder().decode(input);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isSupportedContentType(String contentType) {
        return Objects.nonNull(contentType) && (
                contentType.startsWith("image/") ||
                        contentType.startsWith("video/")
        );
    }
}