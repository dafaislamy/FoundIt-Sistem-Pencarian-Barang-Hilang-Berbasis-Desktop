package com.foundit.apk.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Halaman Profil Pengguna (Konversi dari profil.html, profil.js, dan profil.css).
 */
public class ProfilFrame extends JFrame {

    private final String email, role;

    // Data profil tersimpan di memori selama aplikasi berjalan (menggantikan localStorage "dataUser" di profil.js)
    public static String namaUser = "Mahasiswa FoundIt";
    public static String nimUser = "25071100361";
    public static String fakultasUser = "Fakultas Teknik";
    public static String jurusanUser = "S1 Teknik Informatika";
    public static String angkatanUser = "2025";
    public static String emailUser = "";
    public static String nohpUser = "081234567890";

    private JLabel lblTopName, lblTopInitial;
    private JLabel lblPhoto, lblNamaProfil, lblNimProfil;
    private JLabel valNama, valNim, valFakultas, valJurusan, valAngkatan, valEmail, valNohp;

    public ProfilFrame(String email, String role) {
        this.email = email;
        this.role = role;

        if (emailUser.isEmpty() && email != null && !email.isEmpty()) {
            emailUser = email;
        }

        UiKit.frame(this, "FoundIt | Profil Saya", 1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setContentPane(build());
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiKit.BG);

        // SIDEBAR LENGKAP 8 MENU
        JPanel side = new JPanel();
        side.setBackground(Color.WHITE);
        side.setPreferredSize(new Dimension(238, 0));
        side.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UiKit.BORDER));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));

        JPanel logo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logo.setOpaque(false);
        logo.setBorder(new EmptyBorder(27, 25, 32, 0));
        logo.add(UiKit.label("Found", 27, true, UiKit.BLUE_DARK));
        logo.add(UiKit.label("It", 27, true, UiKit.BLUE));

        side.add(logo);
        side.add(menu("Dashboard", "home", false, () -> {
            new UserDashboardFrame(email, role).setVisible(true);
            dispose();
        }));
        side.add(menu("Cari Barang", "search", false, () -> {
            new CariBarangFrame(email, role).setVisible(true);
            dispose();
        }));
        side.add(menu("Lapor Barang Hilang", "lost", false, () -> {
            new LaporHilangFrame(email, role).setVisible(true);
            dispose();
        }));
        side.add(menu("Lapor Barang Ditemukan", "found", false, () -> {
            new LaporDitemukanFrame(email, role).setVisible(true);
            dispose();
        }));
        side.add(menu("Klaim Barang", "claim", false, () -> {
            new KlaimFrame(email, role).setVisible(true);
            dispose();
        }));
        side.add(menu("Status Klaim", "status", false, () -> {
            new StatusKlaimFrame(email, role).setVisible(true);
            dispose();
        }));
        side.add(menu("Riwayat Laporan", "history", false, () -> {
            new RiwayatLaporanFrame(email, role).setVisible(true);
            dispose();
        }));
        side.add(menu("Profil", "profile", true, () -> {}));

        side.add(Box.createVerticalGlue());

        side.add(menu("Keluar", "return", false, () -> {
            new IndexFrame().setVisible(true);
            dispose();
        }));
        side.add(Box.createVerticalStrut(25));

        root.add(side, BorderLayout.WEST);

        // MAIN CONTENT
        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(26, 34, 25, 34));

        // TOPBAR HEADER
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(
            UiKit.header("Profil Saya", "Kelola informasi akun kamu."),
            BorderLayout.WEST
        );

        JPanel profile = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        profile.setOpaque(false);

        String init = getInitial(namaUser);
        lblTopInitial = UiKit.label(init, 16, true, Color.WHITE);
        lblTopInitial.setHorizontalAlignment(SwingConstants.CENTER);
        lblTopInitial.setOpaque(true);
        lblTopInitial.setBackground(UiKit.BLUE);
        lblTopInitial.setPreferredSize(new Dimension(40, 40));
        profile.add(lblTopInitial);

        JPanel names = new JPanel();
        names.setOpaque(false);
        names.setLayout(new BoxLayout(names, BoxLayout.Y_AXIS));
        lblTopName = UiKit.label(namaUser, 13, true, UiKit.TEXT);
        names.add(lblTopName);
        names.add(UiKit.label("User", 11, false, UiKit.MUTED));
        profile.add(names);

        top.add(profile, BorderLayout.EAST);
        main.add(top, BorderLayout.NORTH);

        // BODY CONTENT (.profile-page)
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(22, 0, 10, 8));

        // 1. HEADER PROFIL (.profile-header)
        JPanel profileHeader = UiKit.card();
        profileHeader.setBackground(Color.WHITE);
        profileHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(237, 240, 242)),
            new EmptyBorder(26, 28, 26, 28)
        ));
        profileHeader.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));
        profileHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 135));

        lblPhoto = UiKit.label(init, 32, true, Color.WHITE);
        lblPhoto.setHorizontalAlignment(SwingConstants.CENTER);
        lblPhoto.setOpaque(true);
        lblPhoto.setBackground(UiKit.BLUE);
        lblPhoto.setPreferredSize(new Dimension(80, 80));
        profileHeader.add(lblPhoto);

        JPanel headerIdentity = new JPanel();
        headerIdentity.setOpaque(false);
        headerIdentity.setLayout(new BoxLayout(headerIdentity, BoxLayout.Y_AXIS));
        lblNamaProfil = UiKit.label(namaUser, 22, true, UiKit.TEXT);
        lblNimProfil = UiKit.label(nimUser, 14, false, UiKit.MUTED);
        headerIdentity.add(lblNamaProfil);
        headerIdentity.add(Box.createVerticalStrut(6));
        headerIdentity.add(lblNimProfil);

        profileHeader.add(headerIdentity);
        body.add(profileHeader);
        body.add(Box.createVerticalStrut(22));

        // 2. KARTU INFORMASI PROFIL (.profile-card)
        JPanel profileCard = UiKit.card();
        profileCard.setBackground(Color.WHITE);
        profileCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(237, 240, 242)),
            new EmptyBorder(28, 30, 30, 30)
        ));
        profileCard.setLayout(new BorderLayout(0, 25));

        // Header Kartu (.profile-card-header)
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);

        JPanel chLeft = new JPanel();
        chLeft.setOpaque(false);
        chLeft.setLayout(new BoxLayout(chLeft, BoxLayout.Y_AXIS));
        chLeft.add(UiKit.label("Informasi Pribadi", 20, true, UiKit.TEXT));
        chLeft.add(Box.createVerticalStrut(5));
        chLeft.add(UiKit.label("Informasi akun yang kamu daftarkan.", 13, false, UiKit.MUTED));
        cardHeader.add(chLeft, BorderLayout.WEST);

        JPanel chRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        chRight.setOpaque(false);
        JButton btnEdit = UiKit.button("Edit Profil", UiKit.BLUE, Color.WHITE);
        spaceButton(btnEdit, 130, 40);
        btnEdit.addActionListener(e -> showEditDialog());
        chRight.add(btnEdit);
        cardHeader.add(chRight, BorderLayout.EAST);

        profileCard.add(cardHeader, BorderLayout.NORTH);

        // Grid Informasi Profil (.profile-info-grid: 2 kolom x 4 baris)
        JPanel infoGrid = new JPanel(new GridLayout(4, 2, 18, 18));
        infoGrid.setOpaque(false);

        valNama = UiKit.label(namaUser, 15, true, UiKit.TEXT);
        valNim = UiKit.label(nimUser, 15, true, UiKit.TEXT);
        valFakultas = UiKit.label(fakultasUser, 15, true, UiKit.TEXT);
        valJurusan = UiKit.label(jurusanUser, 15, true, UiKit.TEXT);
        valAngkatan = UiKit.label(angkatanUser, 15, true, UiKit.TEXT);
        valEmail = UiKit.label(emailUser.isEmpty() ? "-" : emailUser, 15, true, UiKit.TEXT);
        valNohp = UiKit.label(nohpUser, 15, true, UiKit.TEXT);

        infoGrid.add(createInfoItem("Nama Lengkap", valNama));
        infoGrid.add(createInfoItem("NIM", valNim));
        infoGrid.add(createInfoItem("Fakultas", valFakultas));
        infoGrid.add(createInfoItem("Program Studi", valJurusan));
        infoGrid.add(createInfoItem("Angkatan", valAngkatan));
        infoGrid.add(createInfoItem("Email", valEmail));
        infoGrid.add(createInfoItem("Nomor WhatsApp", valNohp));

        // Panel kosong untuk pengisi sel ke-8 agar proporsi grid 2 kolom tetap rapi
        JPanel emptyCell = new JPanel();
        emptyCell.setOpaque(false);
        infoGrid.add(emptyCell);

        profileCard.add(infoGrid, BorderLayout.CENTER);
        body.add(profileCard);

        JScrollPane sc = new JScrollPane(body);
        sc.setBorder(null);
        sc.getViewport().setOpaque(false);
        sc.setOpaque(false);
        sc.getVerticalScrollBar().setUnitIncrement(18);

        main.add(sc, BorderLayout.CENTER);
        root.add(main, BorderLayout.CENTER);

        return root;
    }

    private JPanel createInfoItem(String labelText, JLabel valueLabel) {
        // Kotak Item Informasi (.info-item)
        JPanel item = new JPanel();
        item.setBackground(new Color(247, 249, 252)); // #f7f9fc
        item.setBorder(new EmptyBorder(16, 18, 16, 18));
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));

        JLabel lbl = UiKit.label(labelText, 13, false, new Color(127, 140, 141)); // #7f8c8d
        item.add(lbl);
        item.add(Box.createVerticalStrut(7));
        item.add(valueLabel);

        return item;
    }

    private void showEditDialog() {
        JTextField tfNama = new JTextField(namaUser);
        JTextField tfNim = new JTextField(nimUser);
        JComboBox<String> cbFak = new JComboBox<>(new String[]{
            "Fakultas Teknik",
            "Fakultas Ilmu Komputer",
            "Fakultas Ekonomi dan Bisnis",
            "Fakultas Hukum",
            "Fakultas Pertanian",
            "Fakultas Ilmu Sosial dan Ilmu Politik",
            "Fakultas Matematika dan Ilmu Pengetahuan Alam",
            "Fakultas Keguruan dan Ilmu Pendidikan"
        });
        cbFak.setSelectedItem(fakultasUser);
        JTextField tfJur = new JTextField(jurusanUser);
        JComboBox<String> cbAng = new JComboBox<>(new String[]{"2022", "2023", "2024", "2025", "2026"});
        cbAng.setSelectedItem(angkatanUser);
        JTextField tfEm = new JTextField(emailUser);
        JTextField tfHp = new JTextField(nohpUser);

        Object[] message = {
            "Nama Lengkap:", tfNama,
            "NIM:", tfNim,
            "Fakultas:", cbFak,
            "Program Studi:", tfJur,
            "Angkatan:", cbAng,
            "Email:", tfEm,
            "Nomor WhatsApp:", tfHp
        };

        int opt = JOptionPane.showConfirmDialog(
            this, message, "Edit Profil", JOptionPane.OK_CANCEL_OPTION
        );

        if (opt == JOptionPane.OK_OPTION) {
            if (!tfNama.getText().trim().isEmpty() && !tfNim.getText().trim().isEmpty()) {
                namaUser = tfNama.getText().trim();
                nimUser = tfNim.getText().trim();
                fakultasUser = (String) cbFak.getSelectedItem();
                jurusanUser = tfJur.getText().trim().isEmpty() ? "-" : tfJur.getText().trim();
                angkatanUser = (String) cbAng.getSelectedItem();
                emailUser = tfEm.getText().trim().isEmpty() ? "-" : tfEm.getText().trim();
                nohpUser = tfHp.getText().trim().isEmpty() ? "-" : tfHp.getText().trim();

                refreshDataDisplay();
                JOptionPane.showMessageDialog(this, "Data profil berhasil diperbarui!");
            } else {
                JOptionPane.showMessageDialog(this, "Nama Lengkap dan NIM tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void refreshDataDisplay() {
        String init = getInitial(namaUser);
        lblTopInitial.setText(init);
        lblTopName.setText(namaUser);
        lblPhoto.setText(init);
        lblNamaProfil.setText(namaUser);
        lblNimProfil.setText(nimUser);

        valNama.setText(namaUser);
        valNim.setText(nimUser);
        valFakultas.setText(fakultasUser);
        valJurusan.setText(jurusanUser);
        valAngkatan.setText(angkatanUser);
        valEmail.setText(emailUser);
        valNohp.setText(nohpUser);
    }

    private String getInitial(String name) {
        if (name != null && !name.trim().isEmpty()) {
            return name.trim().substring(0, 1).toUpperCase();
        }
        return (email != null && !email.isEmpty()) ? email.substring(0, 1).toUpperCase() : "U";
    }

    private JPanel menu(String text, String icon, boolean active, Runnable action) {
        JPanel p = new JPanel(new BorderLayout());
        p.setMaximumSize(new Dimension(220, 44));
        p.setBorder(new EmptyBorder(10, 18, 10, 10));
        p.setBackground(active ? UiKit.PALE : Color.WHITE);

        JLabel item = new JLabel(text);
        item.setHorizontalAlignment(SwingConstants.LEFT);
        item.setFont(new Font("Segoe UI", Font.BOLD, 13));
        item.setForeground(active ? UiKit.BLUE : UiKit.MUTED);

        p.add(item);
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
        });

        return p;
    }

    private void spaceButton(JButton button, int width, int height) {
        button.setMargin(new Insets(8, 20, 8, 20));
        button.setBorder(new EmptyBorder(8, 20, 8, 20));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setPreferredSize(new Dimension(width, height));
        button.setMinimumSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
    }
}