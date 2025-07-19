package com.interview.materials.feature.test.inditex.application.validation.error;

import com.interview.materials.feature.test.inditex.domain.exception.DomainValidationException;

public class InvalidSortDirectionException extends DomainValidationException {
    public InvalidSortDirectionException() {
        super("The sort direction must be either 'asc' or 'desc'");
    }

    public InvalidSortDirectionException(String message) {
        super(message);
    }
}
