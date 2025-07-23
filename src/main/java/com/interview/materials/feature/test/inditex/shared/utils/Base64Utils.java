package com.interview.materials.feature.test.inditex.shared.utils;

import com.interview.materials.feature.test.inditex.application.validation.error.InvalidBase64EncodedAssetException;

import java.util.Base64;

public final class Base64Utils {

    private Base64Utils() {
        // Utility class, prevent instantiation
    }

    public static boolean isValidBase64(String input) {
        try {
            Base64.getDecoder().decode(input);
            return true;
        } catch (IllegalArgumentException e) {
            throw new InvalidBase64EncodedAssetException("Invalid base64 encoding");
        }
    }

    public static long decodedLength(String input) {
        return decode(input).length;
    }

    public static byte[] decode(String input) {
        return Base64.getDecoder().decode(input);
    }
}