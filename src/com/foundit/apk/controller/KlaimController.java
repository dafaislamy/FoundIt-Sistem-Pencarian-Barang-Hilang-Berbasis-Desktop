package com.foundit.apk.controller;

import com.foundit.apk.model.ClaimRepository;
import com.foundit.apk.view.KlaimFrame;
import javax.swing.JOptionPane;

/**
 * Controller untuk KlaimFrame.
 */
public class KlaimController {
    private final KlaimFrame view;
    private final String email;
    private final String role;

    public KlaimController(String email, String role) {
        this(email, role, "", "", "");
    }

    public KlaimController(String email, String role, String prefillNama, String prefillKategori, String prefillLokasi) {
        this(new KlaimFrame(email, role, prefillNama, prefillKategori, prefillLokasi, false), email, role);
    }

    public KlaimController(KlaimFrame view, String email, String role) {
        this.view = view;
        this.email = email;
        this.role = role;
        bind();
    }

    public void bind() {
        view.addCancelListener(e -> {
            view.dispose();
            new CariBarangController(email, role).init();
        });

        view.addSubmitListener(e -> handleSubmitKlaim());

        view.setMenuAction("Dashboard", () -> {
            view.dispose();
            new UserDashboardController(email, role).init();
        });
        view.setMenuAction("Cari Barang", () -> {
            view.dispose();
            new CariBarangController(email, role).init();
        });
        view.setMenuAction("Lapor Barang Hilang", () -> {
            view.dispose();
            new ReportController(email, role, "HILANG", "Lapor Barang Hilang").init();
        });
        view.setMenuAction("Lapor Barang Ditemukan", () -> {
            view.dispose();
            new ReportController(email, role, "DITEMUKAN", "Lapor Barang Ditemukan").init();
        });
        view.setMenuAction("Status Klaim", () -> {
            view.dispose();
            new StatusKlaimController(email, role).init();
        });
        view.setMenuAction("Riwayat Laporan", () -> {
            view.dispose();
            new RiwayatLaporanController(email, role).init();
        });
        view.setMenuAction("Profil", () -> {
            view.dispose();
            new ProfilController(email, role).init();
        });
        view.setMenuAction("Keluar", () -> {
            view.dispose();
            new IndexController().init();
        });
    }

    private void handleSubmitKlaim() {
        String nama = view.getNamaBarang();
        String kategori = view.getKategori();
        int katIndex = view.getKategoriSelectedIndex();
        String ciri = view.getCiriBarang();
        String lokasi = view.getLokasiHilang();
        String keterangan = view.getKeterangan();

        if (nama.isEmpty() || katIndex == 0 || ciri.isEmpty() || lokasi.isEmpty() || keterangan.isEmpty()) {
            JOptionPane.showMessageDialog(
                view,
                "Harap lengkapi semua kolom pada form pengajuan klaim!",
                "Data Belum Lengkap",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        ClaimRepository.tambahKlaim(nama, kategori, lokasi, ciri, keterangan);

        JOptionPane.showMessageDialog(
            view,
            "Pengajuan klaim berhasil dikirim!",
            "Berhasil",
            JOptionPane.INFORMATION_MESSAGE
        );

        view.dispose();
        new StatusKlaimController(email, role).init();
    }

    public void init() {
        view.setVisible(true);
    }

    public KlaimFrame getView() {
        return view;
    }
}
