package com.foundit.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Pusat koneksi JDBC ke MySQL.
 * Urutan prioritas konfigurasi: System property (-Ddb.host=...) > db.properties > default.
 * Dilengkapi mekanisme fallback URLClassLoader untuk memuat driver .jar dari folder lib/
 * secara otomatis bila tidak ditemukan di system classpath.
 */
public final class DatabaseConnection {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String PARAMS =
            "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta"
            + "&useUnicode=true&characterEncoding=UTF-8";
    private static final Properties CONFIG = new Properties();
    private static volatile boolean driverLoaded = false;

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

    private static synchronized void ensureDriverLoaded() throws SQLException {
        if (driverLoaded) {
            return;
        }
        try {
            Class.forName(DRIVER);
            driverLoaded = true;
            return;
        } catch (ClassNotFoundException ignored) {
            // Lanjutkan ke fallback URLClassLoader dari folder lib/
        }

        File libDir = new File("lib");
        if (!libDir.isDirectory()) {
            libDir = new File("../lib");
        }

        File[] jars = libDir.isDirectory()
                ? libDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"))
                : null;

        if (jars == null || jars.length == 0) {
            throw new SQLException("Driver MySQL (mysql-connector-j) tidak ditemukan di classpath maupun folder lib/.");
        }

        try {
            List<URL> urls = new ArrayList<>();
            for (File jar : jars) {
                urls.add(jar.toURI().toURL());
            }
            URLClassLoader loader = new URLClassLoader(urls.toArray(new URL[0]), DatabaseConnection.class.getClassLoader());
            Driver driver = (Driver) Class.forName(DRIVER, true, loader).getDeclaredConstructor().newInstance();
            DriverManager.registerDriver(new DriverShim(driver));
            driverLoaded = true;
        } catch (Exception e) {
            throw new SQLException("Driver MySQL (mysql-connector-j) gagal dimuat dari folder lib/: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        ensureDriverLoaded();
        return DriverManager.getConnection(getUrl(), CONFIG.getProperty("db.user"),
                CONFIG.getProperty("db.password"));
    }

    public static void testConnection() throws SQLException {
        try (Connection ignored = getConnection()) {
            // koneksi berhasil lalu langsung ditutup
        }
    }

    /**
     * Wrapper Driver agar DriverManager menerima driver yang dimuat lewat URLClassLoader.
     */
    private static final class DriverShim implements Driver {
        private final Driver delegate;

        DriverShim(Driver delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean acceptsURL(String u) throws SQLException {
            return delegate.acceptsURL(u);
        }

        @Override
        public Connection connect(String u, Properties p) throws SQLException {
            return delegate.connect(u, p);
        }

        @Override
        public int getMajorVersion() {
            return delegate.getMajorVersion();
        }

        @Override
        public int getMinorVersion() {
            return delegate.getMinorVersion();
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
            return delegate.getPropertyInfo(u, p);
        }

        @Override
        public boolean jdbcCompliant() {
            return delegate.jdbcCompliant();
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return delegate.getParentLogger();
        }
    }
}
