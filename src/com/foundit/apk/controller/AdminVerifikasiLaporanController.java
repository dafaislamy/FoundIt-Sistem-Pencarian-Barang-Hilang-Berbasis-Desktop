package com.foundit.apk.controller;

import com.foundit.apk.model.AdminVerifikasiLaporanRepository;
import com.foundit.apk.view.AdminVerifikasiLaporanFrame;
import javax.swing.JOptionPane;

/**
 * Controller untuk AdminVerifikasiLaporanFrame.
 */
public class AdminVerifikasiLaporanController {
    private final AdminVerifikasiLaporanFrame view;
    private final AdminVerifikasiLaporanRepository repo = AdminVerifikasiLaporanRepository.getInstance();

    public AdminVerifikasiLaporanController() {
        this(new AdminVerifikasiLaporanFrame(false));
    }

    public AdminVerifikasiLaporanController(AdminVerifikasiLaporanFrame view) {
        this.view = view;
        bind();
    }

    public void bind() {
        refreshView();

        view.setMenuNavigationHandler(index -> handleNavigation(index));
    }

    private void refreshView() {
        view.renderCards(
            repo.getLaporanList(),
            index -> handleTolak(index),
            index -> handleSetuju(index)
        );
    }

    private void handleTolak(int index) {
        String alasan = JOptionPane.showInputDialog(
            view,
            "Masukkan alasan penolakan laporan:",
            "Tolak Laporan",
            JOptionPane.QUESTION_MESSAGE
        );
        if (alasan != null && !alasan.trim().isEmpty()) {
            repo.removeLaporan(index);
            refreshView();
            JOptionPane.showMessageDialog(view, "⚠️ Laporan telah ditolak.", "Notifikasi", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleSetuju(int index) {
        int confirm = JOptionPane.showConfirmDialog(
            view,
            "Setujui laporan ini agar dapat dilihat oleh mahasiswa?",
            "Konfirmasi Publikasi",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            repo.setApproved(index, true);
            refreshView();
            JOptionPane.showMessageDialog(view, "✅ Laporan berhasil disetujui & dipublikasikan!", "Notifikasi", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void handleNavigation(int index) {
        view.dispose();
        if (index == 0) {
            new AdminDashboardController().init();
        } else if (index == 1) {
            new AdminKelolaKategoriController().init();
        } else if (index == 2) {
            new AdminKelolaLaporanController().init();
        } else if (index == 3) {
            new AdminKelolaUserController().init();
        } else if (index == 4) {
            new AdminUbahStatusController().init();
        } else if (index == 5) {
            new AdminVerifikasiKlaimController().init();
        } else if (index == 6) {
            new AdminVerifikasiLaporanController().init();
        }
    }

    public void init() {
        view.setVisible(true);
    }

    public AdminVerifikasiLaporanFrame getView() {
        return view;
    }
}
