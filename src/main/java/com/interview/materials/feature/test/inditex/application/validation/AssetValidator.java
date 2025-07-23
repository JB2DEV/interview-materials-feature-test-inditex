package com.interview.materials.feature.test.inditex.application.validation;

import com.interview.materials.feature.test.inditex.application.validation.error.InvalidDateRangeException;
import com.interview.materials.feature.test.inditex.application.validation.error.InvalidSortDirectionException;
import com.interview.materials.feature.test.inditex.application.validation.error.UnsupportedAssetContentTypeException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
public class AssetValidator {

    private static final List<String> ALLOWED_SORT_DIRECTIONS = List.of("ASC", "DESC");

    public Mono<Void> validateContentType(String contentType) {
        return Mono.fromRunnable(() -> {
            if (!isSupportedContentType(contentType)) {
                throw new UnsupportedAssetContentTypeException("Unsupported content type: " + contentType);
            }
        });
    }

    public Mono<Void> validateSortDirection(String sortDirection) {
        return Mono.fromRunnable(() -> {
            if (sortDirection != null && !ALLOWED_SORT_DIRECTIONS.contains(sortDirection.toUpperCase(Locale.ROOT))) {
                throw new InvalidSortDirectionException("Invalid sort direction: " + sortDirection);
            }
        });
    }

    public Mono<Void> validateDateRange(LocalDateTime start, LocalDateTime end) {
        return Mono.fromRunnable(() -> {
            if (start != null && end != null && start.isAfter(end)) {
                throw new InvalidDateRangeException("Start date must be before or equal to end date.");
            }
        });
    }

    private boolean isSupportedContentType(String contentType) {
        // For example, we can support image and video types
        return Objects.nonNull(contentType) && (
                contentType.startsWith("image/") ||
                        contentType.startsWith("video/")
        );
    }
}