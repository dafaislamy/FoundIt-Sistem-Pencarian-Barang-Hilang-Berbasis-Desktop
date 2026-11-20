package com.foundit.apk.controller;

import com.foundit.apk.model.ClaimRepository;
import com.foundit.apk.view.StatusKlaimFrame;
import java.util.List;

/**
 * Controller untuk StatusKlaimFrame.
 */
public class StatusKlaimController {
    private final StatusKlaimFrame view;
    private final String email;
    private final String role;

    public StatusKlaimController(String email, String role) {
        this(new StatusKlaimFrame(email, role, false), email, role);
    }

    public StatusKlaimController(StatusKlaimFrame view, String email, String role) {
        this.view = view;
        this.email = email;
        this.role = role;
        bind();
    }

    public void bind() {
        refreshKlaimData();

        view.addAjukanKlaimListener(e -> openKlaim());

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
        view.setMenuAction("Klaim Barang", () -> openKlaim());
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

    private void refreshKlaimData() {
        List<String[]> daftar = ClaimRepository.getDaftarKlaim();
        view.setJumlahKlaimText(daftar.size() + " pengajuan klaim");
        view.renderKlaimList(daftar, () -> openKlaim());
    }

    private void openKlaim() {
        view.dispose();
        new KlaimController(email, role).init();
    }

    public void init() {
        view.setVisible(true);
    }

    public StatusKlaimFrame getView() {
        return view;
    }
}
