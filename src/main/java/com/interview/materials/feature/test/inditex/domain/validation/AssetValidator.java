package com.interview.materials.feature.test.inditex.domain.validation;

import com.interview.materials.feature.test.inditex.domain.exception.InvalidAssetException;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Objects;

@Component
public class AssetValidator {

    public void validateEncodedFile(String encodedFile) {
        if (!isValidBase64(encodedFile)) {
            throw new InvalidAssetException("The encoded file is not valid base64.");
        }
    }

    public void validateContentType(String contentType) {
        if (!isSupportedContentType(contentType)) {
            throw new InvalidAssetException("Unsupported content type: " + contentType);
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