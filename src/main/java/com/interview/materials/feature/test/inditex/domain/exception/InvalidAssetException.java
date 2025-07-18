package com.interview.materials.feature.test.inditex.domain.exception;

public class InvalidAssetException extends RuntimeException {
    public InvalidAssetException(String message) {
        super(message);
    }
}