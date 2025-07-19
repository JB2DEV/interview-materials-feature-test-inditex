package com.interview.materials.feature.test.inditex.application.validation.error;

import com.interview.materials.feature.test.inditex.domain.exception.DomainValidationException;

public class InvalidDateRangeException extends DomainValidationException {
    public InvalidDateRangeException() {
        super("The date range for upload dates is invalid.The start date must be before the end date.");
    }

    public InvalidDateRangeException(String message) {
        super(message);
    }
}
