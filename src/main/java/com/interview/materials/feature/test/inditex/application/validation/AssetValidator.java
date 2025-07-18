package com.interview.materials.feature.test.inditex.application.validation;

import com.interview.materials.feature.test.inditex.application.validation.error.InvalidBase64EncodedAssetException;
import com.interview.materials.feature.test.inditex.application.validation.error.UnsupportedAssetContentTypeException;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Objects;

@Component
public class AssetValidator {

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