package com.foundit.dao;

import java.sql.SQLException;

/** Exception lapisan DAO dengan pesan yang ramah pengguna. */
public class DAOException extends Exception {

    private static final long serialVersionUID = 1L;

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }

    /** Menerjemahkan SQLException (MySQL/MariaDB) menjadi pesan yang mudah dipahami. */
    public static DAOException from(SQLException e) {
        String raw = e.getMessage() == null ? "" : e.getMessage();
        String state = e.getSQLState() == null ? "" : e.getSQLState();
        int code = e.getErrorCode();
        String msg;

        if (code == 1062) {
            msg = duplicateMessage(raw);
        } else if (code == 1451) {
            msg = "Data tidak dapat dihapus/diubah karena masih digunakan oleh data lain.";
        } else if (code == 1452) {
            msg = "Data referensi (kategori/pengguna/barang/admin) tidak valid atau sudah dihapus.";
        } else if (code == 1406) {
            msg = "Teks yang dimasukkan terlalu panjang untuk salah satu kolom.";
        } else if (code == 1045) {
            msg = "Akses ditolak: periksa db.user / db.password pada db.properties.";
        } else if (code == 1049) {
            msg = "Database tidak ditemukan. Buat database lalu import file sql/foundit_web.sql.";
        } else if (code == 1146) {
            msg = "Tabel belum ada. Import file sql/foundit_web.sql ke database terlebih dahulu.";
        } else if (state.startsWith("08") || raw.contains("Communications link failure")) {
            msg = "Tidak dapat terhubung ke server MySQL. Pastikan MySQL/XAMPP berjalan dan host/port benar.";
        } else if (raw.contains("mysql-connector-j")) {
            msg = raw;
        } else {
            msg = "Terjadi kesalahan database: " + raw;
        }
        return new DAOException(msg, e);
    }

    private static String duplicateMessage(String raw) {
        if (raw.contains("'email'") || raw.contains(".email'")) {
            return "Email sudah terdaftar. Gunakan email lain.";
        }
        if (raw.contains("nomor_identitas")) {
            return "Nomor identitas sudah terdaftar.";
        }
        if (raw.contains("nama_kategori")) {
            return "Nama kategori sudah ada.";
        }
        if (raw.contains("PRIMARY")) {
            return "ID data bentrok dengan data lain. Silakan coba simpan ulang.";
        }
        return "Data duplikat: nilai yang harus unik sudah digunakan.";
    }
}
