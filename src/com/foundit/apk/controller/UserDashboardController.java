package com.foundit.apk.controller;

import com.foundit.apk.model.ReportRepository;
import com.foundit.apk.view.UserDashboardFrame;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Controller untuk UserDashboardFrame.
 */
public class UserDashboardController {
    private final UserDashboardFrame view;
    private final String email;
    private final String role;
    private final ReportRepository repo = ReportRepository.getInstance();

    public UserDashboardController(String email, String role) {
        this(new UserDashboardFrame(email, role, false), email, role);
    }

    public UserDashboardController(UserDashboardFrame view, String email, String role) {
        this.view = view;
        this.email = email;
        this.role = role;
        bind();
    }

    public void bind() {
        refreshDashboardData();

        view.addQuickLostListener(e -> openLost());
        view.addQuickFoundListener(e -> openFound());
        view.addQuickSearchListener(e -> openCariBarang());

        view.setMenuAction("Dashboard", () -> {});
        view.setMenuAction("Cari Barang", () -> openCariBarang());
        view.setMenuAction("Lapor Barang Hilang", () -> openLost());
        view.setMenuAction("Lapor Barang Ditemukan", () -> openFound());
        view.setMenuAction("Klaim Barang", () -> openKlaim());
        view.setMenuAction("Status Klaim", () -> openStatusKlaim());
        view.setMenuAction("Riwayat Laporan", () -> openRiwayatLaporan());
        view.setMenuAction("Profil", () -> openProfil());
        view.setMenuAction("Keluar", () -> {
            view.dispose();
            new IndexController().init();
        });
    }

    private void refreshDashboardData() {
        view.updateStats(repo.count("HILANG"), repo.count("DITEMUKAN"), repo.findAll().size());

        List<ReportRepository.Report> reports = repo.findAll();
        if (reports.isEmpty()) {
            List<String[]> samples = new ArrayList<>();
            samples.add(new String[]{
                "Dompet kulit warna hitam", "HILANG", "Integrated Classroom", "Dompet hitam kecil dengan resleting dan kartu identitas"
            });
            samples.add(new String[]{
                "Tumbler biru 600 ml", "DITEMUKAN", "Kantin FMIPA", "Tumbler tertinggal di meja pojok kanan"
            });
            samples.add(new String[]{
                "Flashdisk SanDisk 64GB", "HILANG", "Lab RPL", "Flashdisk warna hitam dengan gantungan merah."
            });
            samples.add(new String[]{
                "Kunci motor dengan gantungan", "DITEMUKAN", "Area Parkir IL", "Ditemukan dekat parkiran depan IL"
            });

            view.renderSampleReports(samples,
                item -> JOptionPane.showMessageDialog(view, "Nama: " + item[0] + "\nStatus: " + item[1] + "\nLokasi: " + item[2] + "\nDeskripsi: " + item[3], "Detail Barang", JOptionPane.INFORMATION_MESSAGE),
                item -> {
                    view.dispose();
                    new KlaimController(email, role, item[0], "Lainnya", item[2]).init();
                }
            );
        } else {
            view.renderReports(reports,
                r -> JOptionPane.showMessageDialog(view, "Nama: " + r.getName() + "\nStatus: " + r.getType() + "\nLokasi: " + r.getLocation() + "\nDeskripsi: " + r.getDescription(), "Detail Barang", JOptionPane.INFORMATION_MESSAGE),
                r -> {
                    view.dispose();
                    new KlaimController(email, role, r.getName(), "Lainnya", r.getLocation()).init();
                }
            );
        }
    }

    private void openCariBarang() {
        view.dispose();
        new CariBarangController(email, role).init();
    }

    private void openLost() {
        view.dispose();
        new ReportController(email, role, "HILANG", "Lapor Barang Hilang").init();
    }

    private void openFound() {
        view.dispose();
        new ReportController(email, role, "DITEMUKAN", "Lapor Barang Ditemukan").init();
    }

    private void openKlaim() {
        view.dispose();
        new KlaimController(email, role).init();
    }

    private void openStatusKlaim() {
        view.dispose();
        new StatusKlaimController(email, role).init();
    }

    private void openRiwayatLaporan() {
        view.dispose();
        new RiwayatLaporanController(email, role).init();
    }

    private void openProfil() {
        view.dispose();
        new ProfilController(email, role).init();
    }

    public void init() {
        view.setVisible(true);
    }

    public UserDashboardFrame getView() {
        return view;
    }
}
