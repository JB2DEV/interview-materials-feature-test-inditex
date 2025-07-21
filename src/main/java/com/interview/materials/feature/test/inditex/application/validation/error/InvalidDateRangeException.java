package com.interview.materials.feature.test.inditex.application.validation.error;

import com.interview.materials.feature.test.inditex.domain.exception.DomainValidationException;

public class InvalidDateRangeException extends DomainValidationException {

    public InvalidDateRangeException(String message) {
        super(message);
    }
}
