package com.foundit.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/** Utilitas validasi input. */
public final class Validator {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9\\-\\s]{8,20}$");

    private Validator() {
    }

    /** Wajib diisi; mengembalikan teks yang sudah di-trim. */
    public static String required(String field, String value, int maxLength) throws ValidationException {
        String v = value == null ? "" : value.trim();
        if (v.isEmpty()) {
            throw new ValidationException(field + " wajib diisi.");
        }
        if (v.length() > maxLength) {
            throw new ValidationException(field + " maksimal " + maxLength + " karakter.");
        }
        return v;
    }

    /** Opsional; mengembalikan null bila kosong. */
    public static String optional(String field, String value, int maxLength) throws ValidationException {
        String v = value == null ? "" : value.trim();
        if (v.isEmpty()) {
            return null;
        }
        if (v.length() > maxLength) {
            throw new ValidationException(field + " maksimal " + maxLength + " karakter.");
        }
        return v;
    }

    public static String email(String value) throws ValidationException {
        String v = required("Email", value, 100);
        if (!EMAIL.matcher(v).matches()) {
            throw new ValidationException("Format email tidak valid (contoh: nama@domain.com).");
        }
        return v;
    }

    public static String optionalPhone(String field, String value) throws ValidationException {
        String v = optional(field, value, 20);
        if (v != null && !PHONE.matcher(v).matches()) {
            throw new ValidationException(field + " harus berupa angka 8-20 digit (boleh diawali +).");
        }
        return v;
    }

    public static LocalDate parseDate(String field, String text) throws ValidationException {
        String v = text == null ? "" : text.trim();
        if (v.isEmpty()) {
            throw new ValidationException(field + " wajib diisi.");
        }
        try {
            return LocalDate.parse(v);
        } catch (DateTimeParseException e) {
            throw new ValidationException(field + " harus berformat yyyy-MM-dd (contoh: 2026-11-20).");
        }
    }

    public static void requireId(String field, String id) throws ValidationException {
        if (id == null || id.isBlank()) {
            throw new ValidationException(field + " wajib dipilih.");
        }
    }
}
