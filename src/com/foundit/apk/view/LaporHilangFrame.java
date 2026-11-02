package com.foundit.apk.view;

/**
 * Halaman khusus laporan barang hilang.
 */
public class LaporHilangFrame extends ReportFormFrame {

    public LaporHilangFrame(String email, String role) {
        super(
            email,
            role,
            "HILANG",
            "Lapor Barang Hilang"
        );
    }
}