package com.foundit.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * Pusat koneksi JDBC ke MySQL.
 * Urutan prioritas konfigurasi: System property (-Ddb.host=...) > db.properties > default.
 */
public final class DatabaseConnection {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String PARAMS =
            "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta"
            + "&useUnicode=true&characterEncoding=UTF-8";
    private static final Properties CONFIG = new Properties();

    static {
        CONFIG.setProperty("db.host", "localhost");
        CONFIG.setProperty("db.port", "3306");
        CONFIG.setProperty("db.name", "foundit_web");
        CONFIG.setProperty("db.user", "root");
        CONFIG.setProperty("db.password", "");

        File file = new File("db.properties");
        if (file.isFile()) {
            try (InputStream in = new FileInputStream(file)) {
                CONFIG.load(in);
            } catch (IOException ignored) {
                // pakai nilai default bila file tidak terbaca
            }
        }
        for (String key : List.of("db.host", "db.port", "db.name", "db.user", "db.password")) {
            String override = System.getProperty(key);
            if (override != null) {
                CONFIG.setProperty(key, override);
            }
        }
    }

    private DatabaseConnection() {
    }

    public static String getUrl() {
        return "jdbc:mysql://" + CONFIG.getProperty("db.host") + ":" + CONFIG.getProperty("db.port")
                + "/" + CONFIG.getProperty("db.name") + PARAMS;
    }

    /** URL tanpa parameter, untuk ditampilkan di UI. */
    public static String getDisplayUrl() {
        return "mysql://" + CONFIG.getProperty("db.host") + ":" + CONFIG.getProperty("db.port")
                + "/" + CONFIG.getProperty("db.name");
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL (mysql-connector-j) tidak ditemukan di classpath. "
                    + "Tambahkan file .jar ke folder lib/.", e);
        }
        return DriverManager.getConnection(getUrl(), CONFIG.getProperty("db.user"),
                CONFIG.getProperty("db.password"));
    }

    public static void testConnection() throws SQLException {
        try (Connection ignored = getConnection()) {
            // koneksi berhasil lalu langsung ditutup
        }
    }
}
