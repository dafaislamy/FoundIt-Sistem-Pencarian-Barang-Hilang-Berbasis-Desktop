package com.foundit.util;

import java.sql.SQLException;

/** Menerjemahkan SQLException menjadi pesan berbahasa Indonesia yang ramah pengguna. */
public final class DbErrors {

    private DbErrors() {
    }

    public static String friendly(SQLException e) {
        String state = e.getSQLState() == null ? "" : e.getSQLState();

        if (state.startsWith("08")) {
            return "Tidak dapat terhubung ke database MySQL. Pastikan server MySQL berjalan "
                    + "dan konfigurasi koneksi sudah benar.";
        }
        switch (e.getErrorCode()) {
            case 1045:
                return "Login database ditolak. Periksa username dan password MySQL.";
            case 1049:
                return "Database belum dibuat. Jalankan skrip database/foundit.sql terlebih dahulu.";
            case 1146:
                return "Tabel database tidak ditemukan. Jalankan ulang skrip database/foundit.sql.";
            case 1062:
                return "Data duplikat: nilai yang bersifat unik (nama/email/username/klaim) sudah ada.";
            case 1451:
                return "Data tidak dapat dihapus/diubah karena masih dipakai oleh data lain.";
            case 1452:
                return "Data relasi tidak valid. Pilih kategori/pengguna/admin/barang yang masih tersedia.";
            case 3819:
                return "Nilai yang dimasukkan melanggar aturan (constraint) database.";
            default:
                return "Terjadi kesalahan database: " + e.getMessage();
        }
    }
}
