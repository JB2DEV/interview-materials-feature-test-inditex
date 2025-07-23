package com.interview.materials.feature.test.inditex.shared.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private DateUtils() {
        // Utility class, prevent instantiation
    }

    public static LocalDateTime parse(String date) {
        if (date == null || date.isBlank()) return null;
        return LocalDateTime.parse(date);
    }

    public static String nowFormatted() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now());
    }

}