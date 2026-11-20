package com.foundit.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/** Kumpulan validasi input yang dipakai oleh lapisan service dan view. */
public final class Validator {

    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9]{8,15}$");
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_.]{4,30}$");

    private Validator() {
    }

    /** Wajib diisi; mengembalikan teks yang sudah di-trim. */
    public static String required(String value, String field, int maxLength) throws AppException {
        String v = value == null ? "" : value.trim();
        if (v.isEmpty()) {
            throw new AppException(field + " wajib diisi.");
        }
        if (v.length() > maxLength) {
            throw new AppException(field + " maksimal " + maxLength + " karakter.");
        }
        return v;
    }

    /** Opsional; mengembalikan null bila kosong. */
    public static String optional(String value, String field, int maxLength) throws AppException {
        String v = value == null ? "" : value.trim();
        if (v.isEmpty()) {
            return null;
        }
        if (v.length() > maxLength) {
            throw new AppException(field + " maksimal " + maxLength + " karakter.");
        }
        return v;
    }

    public static String email(String value) throws AppException {
        String v = required(value, "Email", 120);
        if (!EMAIL.matcher(v).matches()) {
            throw new AppException("Format email tidak valid (contoh: nama@domain.com).");
        }
        return v;
    }

    /** Nomor telepon bersifat opsional; bila diisi harus 8-15 digit (boleh diawali +). */
    public static String phone(String value) throws AppException {
        String v = optional(value, "Nomor telepon", 20);
        if (v != null && !PHONE.matcher(v).matches()) {
            throw new AppException("Nomor telepon harus berupa 8-15 digit angka (boleh diawali +).");
        }
        return v;
    }

    public static String username(String value) throws AppException {
        String v = required(value, "Username", 30);
        if (!USERNAME.matcher(v).matches()) {
            throw new AppException("Username 4-30 karakter: huruf, angka, titik, atau garis bawah.");
        }
        return v;
    }

    public static LocalDate parseDate(String value, String field) throws AppException {
        String v = value == null ? "" : value.trim();
        if (v.isEmpty()) {
            throw new AppException(field + " wajib diisi.");
        }
        try {
            return LocalDate.parse(v);
        } catch (DateTimeParseException e) {
            throw new AppException(field + " harus berformat yyyy-MM-dd (contoh: 2026-11-19).");
        }
    }

    public static void selected(int id, String message) throws AppException {
        if (id <= 0) {
            throw new AppException(message);
        }
    }
}
