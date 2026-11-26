package com.foundit.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Hash password SHA-256 (hex huruf kecil, sama dengan SHA2(x,256) di MySQL). */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hash(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(plain.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 tidak tersedia", e);
        }
    }

    /**
     * Memverifikasi password input terhadap nilai kolom password di database:
     * 1. Hash BCrypt ($2y$ / $2a$ / $2b$)
     * 2. Hash SHA-256 hex (64 karakter)
     * 3. Plain-text langsung (misalnya seed data seperti admin123)
     */
    public static boolean matches(String plain, String stored) {
        if (plain == null || stored == null || stored.isEmpty()) {
            return false;
        }
        if (stored.startsWith("$2y$") || stored.startsWith("$2a$") || stored.startsWith("$2b$")) {
            return BCryptVerifier.verify(plain, stored);
        }
        if (stored.length() == 64 && hash(plain).equalsIgnoreCase(stored)) {
            return true;
        }
        return MessageDigest.isEqual(
                plain.getBytes(StandardCharsets.UTF_8),
                stored.getBytes(StandardCharsets.UTF_8)
        );
    }
}
