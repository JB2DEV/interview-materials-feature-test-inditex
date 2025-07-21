package com.interview.materials.feature.test.inditex.appication.validation;

import com.interview.materials.feature.test.inditex.application.validation.AssetValidator;
import com.interview.materials.feature.test.inditex.application.validation.error.InvalidBase64EncodedAssetException;
import com.interview.materials.feature.test.inditex.application.validation.error.InvalidDateRangeException;
import com.interview.materials.feature.test.inditex.application.validation.error.InvalidSortDirectionException;
import com.interview.materials.feature.test.inditex.application.validation.error.UnsupportedAssetContentTypeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class AssetValidatorTest {

    @InjectMocks
    private AssetValidator assetValidator;

    @Test
    void validateEncodedFile_withValidBase64_shouldNotThrowException() {
        String validBase64 = "dGVzdCBkYXRh";

        StepVerifier.create(assetValidator.validateEncodedFile(validBase64))
                .verifyComplete();
    }

    @Test
    void validateEncodedFile_withInvalidBase64_shouldThrowInvalidBase64EncodedAssetException() {
        String invalidBase64 = "not a valid base64 string!";

        StepVerifier.create(assetValidator.validateEncodedFile(invalidBase64))
                .expectError(InvalidBase64EncodedAssetException.class)
                .verify();
    }

    @Test
    void validateContentType_withSupportedTypeImage_shouldNotThrowException() {
        String contentType = "image/png";

        StepVerifier.create(assetValidator.validateContentType(contentType))
                .verifyComplete();
    }

    @Test
    void validateContentType_withSupportedTypeVideo_shouldNotThrowException() {
        String contentType = "video/mp4";

        StepVerifier.create(assetValidator.validateContentType(contentType))
                .verifyComplete();
    }

    @Test
    void validateContentType_withUnsupportedType_shouldThrowUnsupportedAssetContentTypeException() {
        String contentType = "other/png";

        StepVerifier.create(assetValidator.validateContentType(contentType))
                .expectError(UnsupportedAssetContentTypeException.class)
                .verify();
    }

    @Test
    void validateContentType_withNull_shouldThrowUnsupportedAssetContentTypeException() {
        StepVerifier.create(assetValidator.validateContentType(null))
                .expectError(UnsupportedAssetContentTypeException.class)
                .verify();
    }

    @Test
    void validateSortDirection_withASC_shouldNotThrowException() {
        String sortDirection = "ASC";

        StepVerifier.create(assetValidator.validateSortDirection(sortDirection))
                .verifyComplete();
    }

    @Test
    void validateSortDirection_withDESC_shouldNotThrowException() {
        String sortDirection = "DESC";

        StepVerifier.create(assetValidator.validateSortDirection(sortDirection))
                .verifyComplete();
    }

    @Test
    void validateSortDirection_withLowercaseAsc_shouldNotThrowException() {
        String sortDirection = "asc";

        StepVerifier.create(assetValidator.validateSortDirection(sortDirection))
                .verifyComplete();
    }

    @Test
    void validateSortDirection_withLowercaseDesc_shouldNotThrowException() {
        String sortDirection = "desc";

        StepVerifier.create(assetValidator.validateSortDirection(sortDirection))
                .verifyComplete();
    }

    @Test
    void validateSortDirection_withInvalidDirection_shouldThrowInvalidSortDirectionException() {
        String invalidDirection = "INVALID";

        StepVerifier.create(assetValidator.validateSortDirection(invalidDirection))
                .expectError(InvalidSortDirectionException.class)
                .verify();
    }

    @Test
    void validateSortDirection_withNull_shouldNotThrowException() {
        StepVerifier.create(assetValidator.validateSortDirection(null))
                .verifyComplete();
    }

    @Test
    void validateDateRange_withValidRange_shouldNotThrowException() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);

        StepVerifier.create(assetValidator.validateDateRange(start, end))
                .verifyComplete();
    }

    @Test
    void validateDateRange_withEqualDates_shouldNotThrowException() {
        LocalDateTime date = LocalDateTime.now();

        StepVerifier.create(assetValidator.validateDateRange(date, date))
                .verifyComplete();
    }

    @Test
    void validateDateRange_withInvalidRange_shouldThrowInvalidDateRangeException() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.minusHours(1);

        StepVerifier.create(assetValidator.validateDateRange(start, end))
                .expectError(InvalidDateRangeException.class)
                .verify();
    }

    @Test
    void validateDateRange_withNullStart_shouldNotThrowException() {
        LocalDateTime end = LocalDateTime.now();

        StepVerifier.create(assetValidator.validateDateRange(null, end))
                .verifyComplete();
    }

    @Test
    void validateDateRange_withNullEnd_shouldNotThrowException() {
        LocalDateTime start = LocalDateTime.now();

        StepVerifier.create(assetValidator.validateDateRange(start, null))
                .verifyComplete();
    }

    @Test
    void validateDateRange_withBothNull_shouldNotThrowException() {
        StepVerifier.create(assetValidator.validateDateRange(null, null))
                .verifyComplete();
    }
}