package com.interview.materials.feature.test.inditex.shared.enums;

import com.interview.materials.feature.test.inditex.application.validation.error.InvalidSortDirectionException;

import java.util.Locale;

public enum SortDirection {
    ASC,
    DESC;

    public static SortDirection from(String value) {
        try {
            return SortDirection.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            throw new InvalidSortDirectionException("Invalid sort direction: " + value);
        }
    }
}