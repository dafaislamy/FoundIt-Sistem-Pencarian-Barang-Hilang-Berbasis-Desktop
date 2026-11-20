package com.foundit.apk.controller;

import com.foundit.apk.model.UserProfileModel;
import com.foundit.apk.view.ProfilFrame;
import javax.swing.JOptionPane;

/**
 * Controller untuk ProfilFrame.
 */
public class ProfilController {
    private final ProfilFrame view;
    private final String email;
    private final String role;

    public ProfilController(String email, String role) {
        this(new ProfilFrame(email, role, false), email, role);
    }

    public ProfilController(ProfilFrame view, String email, String role) {
        this.view = view;
        this.email = email;
        this.role = role;
        bind();
    }

    public void bind() {
        if (UserProfileModel.getEmailUser().isEmpty() && email != null && !email.isEmpty()) {
            UserProfileModel.setEmailUser(email);
        }

        refreshView();

        view.addEditProfileListener(e -> handleEditProfile());

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
        view.setMenuAction("Klaim Barang", () -> {
            view.dispose();
            new KlaimController(email, role).init();
        });
        view.setMenuAction("Status Klaim", () -> {
            view.dispose();
            new StatusKlaimController(email, role).init();
        });
        view.setMenuAction("Riwayat Laporan", () -> {
            view.dispose();
            new RiwayatLaporanController(email, role).init();
        });
        view.setMenuAction("Keluar", () -> {
            view.dispose();
            new IndexController().init();
        });
    }

    private void refreshView() {
        String nama = UserProfileModel.getNamaUser();
        String nim = UserProfileModel.getNimUser();
        String fak = UserProfileModel.getFakultasUser();
        String jur = UserProfileModel.getJurusanUser();
        String ang = UserProfileModel.getAngkatanUser();
        String em = UserProfileModel.getEmailUser();
        String hp = UserProfileModel.getNohpUser();
        String init = UserProfileModel.getInitial(email);

        view.updateProfileDisplay(nama, nim, fak, jur, ang, em.isEmpty() ? "-" : em, hp, init);
    }

    private void handleEditProfile() {
        view.promptEditDialog(
            UserProfileModel.getNamaUser(),
            UserProfileModel.getNimUser(),
            UserProfileModel.getFakultasUser(),
            UserProfileModel.getJurusanUser(),
            UserProfileModel.getAngkatanUser(),
            UserProfileModel.getEmailUser(),
            UserProfileModel.getNohpUser(),
            updatedData -> {
                String n = updatedData[0];
                String id = updatedData[1];
                String fak = updatedData[2];
                String jur = updatedData[3];
                String ang = updatedData[4];
                String em = updatedData[5];
                String hp = updatedData[6];

                if (!n.isEmpty() && !id.isEmpty()) {
                    UserProfileModel.updateProfile(n, id, fak, jur, ang, em, hp);
                    refreshView();
                    JOptionPane.showMessageDialog(view, "Data profil berhasil diperbarui!");
                } else {
                    JOptionPane.showMessageDialog(view, "Nama Lengkap dan NIM tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                }
            }
        );
    }

    public void init() {
        view.setVisible(true);
    }

    public ProfilFrame getView() {
        return view;
    }
}
