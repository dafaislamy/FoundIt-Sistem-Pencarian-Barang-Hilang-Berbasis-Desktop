package com.foundit.apk.model;

/**
 * Konfigurasi koneksi database MySQL untuk aplikasi FoundIt.
 * Menggunakan default local XAMPP (localhost:3306, user root, password kosong),
 * dan mendukung penyesuaian melalui Environment Variable maupun System Property.
 */
public class DatabaseConfig {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 3306;
    private static final String DEFAULT_DATABASE = "foundit_web";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private DatabaseConfig() {
        // Utility class
    }

    public static String getHost() {
        return System.getProperty("foundit.db.host",
                System.getenv().getOrDefault("FOUNDIT_DB_HOST", DEFAULT_HOST));
    }

    public static int getPort() {
        String portStr = System.getProperty("foundit.db.port",
                System.getenv().getOrDefault("FOUNDIT_DB_PORT", String.valueOf(DEFAULT_PORT)));
        try {
            return Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            return DEFAULT_PORT;
        }
    }

    public static String getDatabaseName() {
        return System.getProperty("foundit.db.name",
                System.getenv().getOrDefault("FOUNDIT_DB_NAME", DEFAULT_DATABASE));
    }

    public static String getUser() {
        return System.getProperty("foundit.db.user",
                System.getenv().getOrDefault("FOUNDIT_DB_USER", DEFAULT_USER));
    }

    public static String getPassword() {
        return System.getProperty("foundit.db.password",
                System.getenv().getOrDefault("FOUNDIT_DB_PASSWORD", DEFAULT_PASSWORD));
    }

    public static String getJdbcUrl() {
        return String.format(
                "jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta&characterEncoding=UTF-8",
                getHost(), getPort(), getDatabaseName()
        );
    }
}
