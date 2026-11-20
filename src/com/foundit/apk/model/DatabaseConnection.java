package com.foundit.apk.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Penyedia koneksi JDBC MySQL untuk package model aplikasi FoundIt.
 */
public class DatabaseConnection {
    private static Connection connection = null;

    private DatabaseConnection() {
        // Utility class
    }

    /**
     * Memeriksa ketersediaan driver JDBC MySQL di classpath.
     */
    public static boolean isDriverAvailable() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return true;
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                return true;
            } catch (ClassNotFoundException ex) {
                return false;
            }
        }
    }

    /**
     * Mendapatkan koneksi aktif ke database MySQL foundit_web.
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            if (isDriverAvailable()) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException ignored) {
                }
            }
            connection = DriverManager.getConnection(
                    DatabaseConfig.getJdbcUrl(),
                    DatabaseConfig.getUser(),
                    DatabaseConfig.getPassword()
            );
        }
        return connection;
    }

    /**
     * Menguji apakah koneksi ke database MySQL berhasil dibuat.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Info Database: Belum dapat terhubung ke MySQL (" + e.getMessage() + ")");
            return false;
        }
    }

    /**
     * Menutup koneksi database aktif jika ada.
     */
    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Gagal menutup koneksi database: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
