package com.interview.materials.feature.test.inditex.shared.utils;

import java.time.LocalDateTime;

public class DateParser {

    private DateParser() {
        // Utility class, prevent instantiation
    }

    public static LocalDateTime parse(String date) {
        if (date == null || date.isBlank()) return null;
        return LocalDateTime.parse(date);
    }

}