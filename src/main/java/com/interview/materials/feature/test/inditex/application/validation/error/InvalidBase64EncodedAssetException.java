package com.interview.materials.feature.test.inditex.application.validation.error;


import com.interview.materials.feature.test.inditex.domain.exception.DomainValidationException;

public class InvalidBase64EncodedAssetException extends DomainValidationException {

    public InvalidBase64EncodedAssetException(String message) {
        super(message);
    }
}
