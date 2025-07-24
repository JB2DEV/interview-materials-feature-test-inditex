package com.interview.materials.feature.test.inditex.application.validation.error;

import com.interview.materials.feature.test.inditex.domain.exception.DomainValidationException;

public class MissingCredentialsException extends DomainValidationException {
    public MissingCredentialsException(String message) {
        super(message);
    }
}