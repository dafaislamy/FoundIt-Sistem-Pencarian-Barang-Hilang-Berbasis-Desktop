package com.foundit.apk.controller;

import com.foundit.apk.model.ReportRepository;
import com.foundit.apk.view.LaporDitemukanFrame;
import com.foundit.apk.view.LaporHilangFrame;
import com.foundit.apk.view.ReportFormFrame;
import com.foundit.apk.view.UiKit;
import java.time.LocalDate;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Controller untuk ReportFormFrame (LaporHilangFrame dan LaporDitemukanFrame).
 */
public class ReportController {
    private final ReportFormFrame view;
    private final String email;
    private final String role;
    private final String type;
    private final String title;

    public ReportController(String email, String role, String type, String title) {
        this(type.equalsIgnoreCase("HILANG") 
                ? new LaporHilangFrame(email, role, false) 
                : new LaporDitemukanFrame(email, role, false),
             email, role, type, title);
    }

    public ReportController(ReportFormFrame view, String email, String role, String type, String title) {
        this.view = view;
        this.email = email;
        this.role = role;
        this.type = type;
        this.title = title;
        bind();
    }

    public void bind() {
        view.addChoosePhotoListener(e -> choosePhoto());
        view.addResetListener(e -> view.resetFormFields());
        view.addCancelListener(e -> openDashboard());
        view.addBackListener(e -> openDashboard());
        view.addSubmitListener(e -> handleSubmit());

        view.setMenuAction("Dashboard", () -> openDashboard());
        view.setMenuAction("Cari Barang", () -> {
            view.dispose();
            new CariBarangController(email, role).init();
        });
        view.setMenuAction("Lapor Barang Hilang", () -> {
            if (!type.equals("HILANG")) {
                view.dispose();
                new ReportController(email, role, "HILANG", "Lapor Barang Hilang").init();
            }
        });
        view.setMenuAction("Lapor Barang Ditemukan", () -> {
            if (!type.equals("DITEMUKAN")) {
                view.dispose();
                new ReportController(email, role, "DITEMUKAN", "Lapor Barang Ditemukan").init();
            }
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
        view.setMenuAction("Profil", () -> {
            view.dispose();
            new ProfilController(email, role).init();
        });
        view.setMenuAction("Keluar", () -> {
            view.dispose();
            new IndexController().init();
        });
    }

    private void choosePhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih foto barang");
        if (chooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            view.setPhotoInput(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void handleSubmit() {
        String name = view.getNameInput();
        String loc = view.getLocationInput();
        String desc = view.getDescriptionInput();
        String cat = view.getCategoryInput();
        String dateStr = view.getDateInput();
        String photo = view.getPhotoInput();

        if (name.isEmpty() || loc.isEmpty() || desc.isEmpty()) {
            JOptionPane.showMessageDialog(
                view,
                "Nama barang, lokasi, dan deskripsi wajib diisi.",
                "Data belum lengkap",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        LocalDate d;
        try {
            d = LocalDate.parse(dateStr);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                view,
                "Tanggal harus menggunakan format YYYY-MM-DD.",
                "Tanggal tidak valid",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        ReportRepository.getInstance().add(
            new ReportRepository.Report(name, type, cat, loc, desc, d, photo)
        );

        UiKit.info(view, "Laporan berhasil ditambahkan untuk sesi aplikasi ini.");
        view.dispose();
        new RiwayatLaporanController(email, role).init();
    }

    private void openDashboard() {
        view.dispose();
        new UserDashboardController(email, role).init();
    }

    public void init() {
        view.setVisible(true);
    }

    public ReportFormFrame getView() {
        return view;
    }
}
