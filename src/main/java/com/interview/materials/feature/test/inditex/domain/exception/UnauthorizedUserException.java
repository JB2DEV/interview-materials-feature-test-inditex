package com.interview.materials.feature.test.inditex.domain.exception;

public class UnauthorizedUserException extends RuntimeException {
    public UnauthorizedUserException(String message) {
        super(message);
    }
}
