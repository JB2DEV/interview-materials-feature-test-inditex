package com.interview.materials.feature.test.inditex.application.validation.error;

import com.interview.materials.feature.test.inditex.domain.exception.DomainValidationException;

public class UnsupportedAssetContentTypeException extends DomainValidationException {

    public UnsupportedAssetContentTypeException () {
        super("The content type of the asset is not supported");
    }

    public UnsupportedAssetContentTypeException (String message) {
        super(message);
    }
}
