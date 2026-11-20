package com.foundit.apk.controller;

import com.foundit.apk.model.*;
import com.foundit.apk.view.AdminVerifikasiKlaimFrame;
import java.time.LocalDate;
import javax.swing.JOptionPane;

/**
 * Controller untuk AdminVerifikasiKlaimFrame.
 */
public class AdminVerifikasiKlaimController {
    private final AdminVerifikasiKlaimFrame view;
    private final User adminUser;
    private final User pemohon;
    private final Item barangTemuan;
    private final Claim claimTiket;

    public AdminVerifikasiKlaimController() {
        this(new AdminVerifikasiKlaimFrame(false));
    }

    public AdminVerifikasiKlaimController(AdminVerifikasiKlaimFrame view) {
        this.view = view;
        this.adminUser = new User("ADM1", "Admin Kampus", "999", "admin@univ.ac.id", "123", "0811", Role.ADMIN);
        this.pemohon = new User("U100", "Ahmad Fauzi", "220104112", "ahmad@student.ac.id", "123", "081234", Role.MAHASISWA);
        this.barangTemuan = new Item("ITM1", "Kunci Motor Yamaha", "Remote alarm & gantungan", ItemType.FIND, "Parkir Barat", LocalDate.now(), null, null);
        this.claimTiket = new Claim("KLM-092", "Kunci Yamaha Aerox, gantungan tim Futsal Elektro", barangTemuan, pemohon);
        bind();
    }

    public void bind() {
        view.addSetujuListener(e -> {
            claimTiket.approve(adminUser);
            view.setClaimStatusText(claimTiket.getStatusKlaim().toString());
            JOptionPane.showMessageDialog(view, "Klaim berhasil disetujui!");
            view.dispose();
            new AdminDashboardController().init();
        });

        view.addTolakListener(e -> {
            claimTiket.reject(adminUser);
            view.setClaimStatusText(claimTiket.getStatusKlaim().toString());
            JOptionPane.showMessageDialog(view, "Klaim ditolak.");
            view.dispose();
            new AdminDashboardController().init();
        });

        view.setMenuNavigationHandler(index -> handleNavigation(index));
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

    public AdminVerifikasiKlaimFrame getView() {
        return view;
    }
}
