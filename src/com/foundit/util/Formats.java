package com.foundit.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Format tampilan tanggal/waktu. */
public final class Formats {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Formats() {
    }

    public static String dateTime(LocalDateTime value) {
        return value == null ? "-" : value.format(DATE_TIME);
    }

    public static String orDash(String value) {
        return (value == null || value.isEmpty()) ? "-" : value;
    }
}
