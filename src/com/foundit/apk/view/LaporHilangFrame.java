package com.foundit.apk.view;

/**
 * Halaman khusus laporan barang hilang (View).
 */
public class LaporHilangFrame extends ReportFormFrame {

    public LaporHilangFrame(String email, String role) {
        this(email, role, true);
    }

    public LaporHilangFrame(String email, String role, boolean bindDefaultController) {
        super(email, role, "HILANG", "Lapor Barang Hilang", bindDefaultController);
    }
}
