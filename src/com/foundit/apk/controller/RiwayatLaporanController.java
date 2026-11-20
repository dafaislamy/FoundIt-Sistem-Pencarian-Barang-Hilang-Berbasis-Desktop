package com.foundit.apk.controller;

import com.foundit.apk.model.ReportRepository;
import com.foundit.apk.view.RiwayatLaporanFrame;
import java.util.List;

/**
 * Controller untuk RiwayatLaporanFrame.
 */
public class RiwayatLaporanController {
    private final RiwayatLaporanFrame view;
    private final String email;
    private final String role;
    private final ReportRepository repo = ReportRepository.getInstance();

    public RiwayatLaporanController(String email, String role) {
        this(new RiwayatLaporanFrame(email, role, false), email, role);
    }

    public RiwayatLaporanController(RiwayatLaporanFrame view, String email, String role) {
        this.view = view;
        this.email = email;
        this.role = role;
        bind();
    }

    public void bind() {
        refreshRiwayat();

        view.addBuatLaporanListener(e -> openBuatLaporan());

        view.setMenuAction("Dashboard", () -> {
            view.dispose();
            new UserDashboardController(email, role).init();
        });
        view.setMenuAction("Cari Barang", () -> {
            view.dispose();
            new CariBarangController(email, role).init();
        });
        view.setMenuAction("Lapor Barang Hilang", () -> openBuatLaporan());
        view.setMenuAction("Lapor Barang Ditemukan", () -> {
            view.dispose();
            new ReportController(email, role, "DITEMUKAN", "Lapor Barang Ditemukan").init();
        });
        view.setMenuAction("Klaim Barang", () -> {
            view.dispose();
            new KlaimController(email, role).init();
        });
        view.setMenuAction("Status Klaim", () -> {
            view.dispose();
            new StatusKlaimController(email, role).init();
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

    private void refreshRiwayat() {
        List<ReportRepository.Report> daftar = repo.findAll();
        view.setJumlahLaporanText(daftar.size() + " laporan ditemukan");
        view.renderRiwayatList(daftar, () -> openBuatLaporan());
    }

    private void openBuatLaporan() {
        view.dispose();
        new ReportController(email, role, "HILANG", "Lapor Barang Hilang").init();
    }

    public void init() {
        view.setVisible(true);
    }

    public RiwayatLaporanFrame getView() {
        return view;
    }
}
