package com.foundit.apk.view;

/**
 * Halaman khusus laporan barang ditemukan (View).
 */
public class LaporDitemukanFrame extends ReportFormFrame {

    public LaporDitemukanFrame(String email, String role) {
        this(email, role, true);
    }

    public LaporDitemukanFrame(String email, String role, boolean bindDefaultController) {
        super(email, role, "DITEMUKAN", "Lapor Barang Ditemukan", bindDefaultController);
    }
}
