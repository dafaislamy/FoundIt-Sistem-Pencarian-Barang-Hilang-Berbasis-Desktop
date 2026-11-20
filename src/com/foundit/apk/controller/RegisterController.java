package com.foundit.apk.controller;

import com.foundit.apk.model.UserProfileModel;
import com.foundit.apk.view.RegisterFrame;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Controller untuk RegisterFrame.
 */
public class RegisterController {
    private final RegisterFrame view;
    private final String email;
    private final String role;
    private String selectedPhotoPath = "";

    public RegisterController(String email, String role) {
        this(new RegisterFrame(email, role, false), email, role);
    }

    public RegisterController(RegisterFrame view, String email, String role) {
        this.view = view;
        this.email = email;
        this.role = role;
        bind();
    }

    public void bind() {
        view.addChoosePhotoListener(e -> choosePhoto());

        view.addNamaKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                capitalizeName();
            }
        });

        view.addSubmitListener(e -> handleRegister());

        view.setMenuAction("Dashboard", () -> {
            view.dispose();
            new UserDashboardController(email, role).init();
        });
        view.setMenuAction("Lapor Barang Hilang", () -> {
            view.dispose();
            new ReportController(email, role, "HILANG", "Lapor Barang Hilang").init();
        });
        view.setMenuAction("Lapor Barang Ditemukan", () -> {
            view.dispose();
            new ReportController(email, role, "DITEMUKAN", "Lapor Barang Ditemukan").init();
        });
        view.setMenuAction("Riwayat Laporan", () -> {
            view.dispose();
            new RiwayatLaporanController(email, role).init();
        });
        view.setMenuAction("Status Klaim", () -> {
            view.dispose();
            new StatusKlaimController(email, role).init();
        });
        view.setMenuAction("Keluar", () -> {
            view.dispose();
            new IndexController().init();
        });
    }

    private void choosePhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih Foto Profil");
        if (chooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            selectedPhotoPath = file.getAbsolutePath();
            view.setFotoPath(file.getName(), new Color(39, 174, 96));
        }
    }

    private void capitalizeName() {
        String text = view.getNama();
        if (!text.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            boolean cap = true;
            for (char c : text.toCharArray()) {
                if (Character.isWhitespace(c)) {
                    cap = true;
                    sb.append(c);
                } else if (cap) {
                    sb.append(Character.toUpperCase(c));
                    cap = false;
                } else {
                    sb.append(c);
                }
            }
            if (!text.equals(sb.toString())) {
                int pos = view.getNamaCaretPosition();
                view.setNama(sb.toString());
                view.setNamaCaretPosition(Math.min(pos, sb.length()));
            }
        }
    }

    private void handleRegister() {
        String nama = view.getNama().trim();
        String nim = view.getNim();
        String fakultas = view.getFakultas();
        String jurusan = view.getJurusan();
        String angkatan = view.getAngkatan();
        String inputEmail = view.getEmail();
        String nohp = view.getNoHp();

        if (nama.isEmpty() || nim.isEmpty() || "Pilih Fakultas".equals(fakultas) || jurusan.isEmpty()
                || "Pilih Angkatan".equals(angkatan) || inputEmail.isEmpty() || nohp.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Harap lengkapi semua kolom data mahasiswa!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserProfileModel.updateProfile(nama, nim, fakultas, jurusan, angkatan, inputEmail, nohp);

        JOptionPane.showMessageDialog(view, "Profil berhasil disimpan! Selamat datang, " + nama);
        view.dispose();
        new UserDashboardController(inputEmail, role).init();
    }

    public void init() {
        view.setVisible(true);
    }

    public RegisterFrame getView() {
        return view;
    }
}
