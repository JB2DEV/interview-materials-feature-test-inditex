package com.interview.materials.feature.test.inditex.application.validation.error;

import com.interview.materials.feature.test.inditex.domain.exception.DomainValidationException;

public class InvalidSortDirectionException extends DomainValidationException {

    public InvalidSortDirectionException(String message) {
        super(message);
    }
}
