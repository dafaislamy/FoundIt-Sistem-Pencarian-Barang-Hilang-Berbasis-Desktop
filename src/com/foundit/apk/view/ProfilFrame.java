package com.foundit.apk.view;

import com.foundit.apk.controller.ProfilController;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Halaman Profil Pengguna (View murni).
 */
public class ProfilFrame extends JFrame {

    private final String email, role;
    private JLabel lblTopName, lblTopInitial;
    private JLabel lblPhoto, lblNamaProfil, lblNimProfil;
    private JLabel valNama, valNim, valFakultas, valJurusan, valAngkatan, valEmail, valNohp;
    private JButton btnEdit;
    private final Map<String, Runnable> menuActions = new HashMap<>();

    public ProfilFrame(String email, String role) {
        this(email, role, true);
    }

    public ProfilFrame(String email, String role, boolean bindDefaultController) {
        this.email = email;
        this.role = role;

        UiKit.frame(this, "FoundIt | Profil Saya", 1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setContentPane(build());

        if (bindDefaultController) {
            new ProfilController(this, email, role).bind();
        }
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
        side.add(menu("Dashboard", "home", false, () -> triggerMenu("Dashboard")));
        side.add(menu("Cari Barang", "search", false, () -> triggerMenu("Cari Barang")));
        side.add(menu("Lapor Barang Hilang", "lost", false, () -> triggerMenu("Lapor Barang Hilang")));
        side.add(menu("Lapor Barang Ditemukan", "found", false, () -> triggerMenu("Lapor Barang Ditemukan")));
        side.add(menu("Klaim Barang", "claim", false, () -> triggerMenu("Klaim Barang")));
        side.add(menu("Status Klaim", "status", false, () -> triggerMenu("Status Klaim")));
        side.add(menu("Riwayat Laporan", "history", false, () -> triggerMenu("Riwayat Laporan")));
        side.add(menu("Profil", "profile", true, () -> {}));

        side.add(Box.createVerticalGlue());

        side.add(menu("Keluar", "return", false, () -> triggerMenu("Keluar")));
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

        lblTopInitial = UiKit.label("U", 16, true, Color.WHITE);
        lblTopInitial.setHorizontalAlignment(SwingConstants.CENTER);
        lblTopInitial.setOpaque(true);
        lblTopInitial.setBackground(UiKit.BLUE);
        lblTopInitial.setPreferredSize(new Dimension(40, 40));
        profile.add(lblTopInitial);

        JPanel names = new JPanel();
        names.setOpaque(false);
        names.setLayout(new BoxLayout(names, BoxLayout.Y_AXIS));
        lblTopName = UiKit.label("Pengguna FoundIt", 13, true, UiKit.TEXT);
        names.add(lblTopName);
        names.add(UiKit.label("User", 11, false, UiKit.MUTED));
        profile.add(names);

        top.add(profile, BorderLayout.EAST);
        main.add(top, BorderLayout.NORTH);

        // BODY CONTENT
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(22, 0, 10, 8));

        // 1. HEADER PROFIL
        JPanel profileHeader = UiKit.card();
        profileHeader.setBackground(Color.WHITE);
        profileHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(237, 240, 242)),
            new EmptyBorder(26, 28, 26, 28)
        ));
        profileHeader.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));
        profileHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 135));

        lblPhoto = UiKit.label("U", 32, true, Color.WHITE);
        lblPhoto.setHorizontalAlignment(SwingConstants.CENTER);
        lblPhoto.setOpaque(true);
        lblPhoto.setBackground(UiKit.BLUE);
        lblPhoto.setPreferredSize(new Dimension(80, 80));
        profileHeader.add(lblPhoto);

        JPanel headerIdentity = new JPanel();
        headerIdentity.setOpaque(false);
        headerIdentity.setLayout(new BoxLayout(headerIdentity, BoxLayout.Y_AXIS));
        lblNamaProfil = UiKit.label("Mahasiswa FoundIt", 22, true, UiKit.TEXT);
        lblNimProfil = UiKit.label("25071100361", 14, false, UiKit.MUTED);
        headerIdentity.add(lblNamaProfil);
        headerIdentity.add(Box.createVerticalStrut(6));
        headerIdentity.add(lblNimProfil);

        profileHeader.add(headerIdentity);
        body.add(profileHeader);
        body.add(Box.createVerticalStrut(22));

        // 2. KARTU INFORMASI PROFIL
        JPanel profileCard = UiKit.card();
        profileCard.setBackground(Color.WHITE);
        profileCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(237, 240, 242)),
            new EmptyBorder(28, 30, 30, 30)
        ));
        profileCard.setLayout(new BorderLayout(0, 25));

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
        btnEdit = UiKit.button("Edit Profil", UiKit.BLUE, Color.WHITE);
        spaceButton(btnEdit, 130, 40);
        chRight.add(btnEdit);
        cardHeader.add(chRight, BorderLayout.EAST);

        profileCard.add(cardHeader, BorderLayout.NORTH);

        JPanel infoGrid = new JPanel(new GridLayout(4, 2, 18, 18));
        infoGrid.setOpaque(false);

        valNama = UiKit.label("-", 15, true, UiKit.TEXT);
        valNim = UiKit.label("-", 15, true, UiKit.TEXT);
        valFakultas = UiKit.label("-", 15, true, UiKit.TEXT);
        valJurusan = UiKit.label("-", 15, true, UiKit.TEXT);
        valAngkatan = UiKit.label("-", 15, true, UiKit.TEXT);
        valEmail = UiKit.label("-", 15, true, UiKit.TEXT);
        valNohp = UiKit.label("-", 15, true, UiKit.TEXT);

        infoGrid.add(createInfoItem("Nama Lengkap", valNama));
        infoGrid.add(createInfoItem("NIM", valNim));
        infoGrid.add(createInfoItem("Fakultas", valFakultas));
        infoGrid.add(createInfoItem("Program Studi", valJurusan));
        infoGrid.add(createInfoItem("Angkatan", valAngkatan));
        infoGrid.add(createInfoItem("Email", valEmail));
        infoGrid.add(createInfoItem("Nomor WhatsApp", valNohp));

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

    public void updateProfileDisplay(String nama, String nim, String fakultas, String jurusan, String angkatan, String email, String nohp, String initial) {
        lblTopInitial.setText(initial);
        lblTopName.setText(nama);
        lblPhoto.setText(initial);
        lblNamaProfil.setText(nama);
        lblNimProfil.setText(nim);

        valNama.setText(nama);
        valNim.setText(nim);
        valFakultas.setText(fakultas);
        valJurusan.setText(jurusan);
        valAngkatan.setText(angkatan);
        valEmail.setText(email);
        valNohp.setText(nohp);
    }

    public void promptEditDialog(String currNama, String currNim, String currFakultas, String currJurusan,
                                 String currAngkatan, String currEmail, String currNohp, Consumer<String[]> onSave) {
        JTextField tfNama = new JTextField(currNama);
        JTextField tfNim = new JTextField(currNim);
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
        cbFak.setSelectedItem(currFakultas);
        JTextField tfJur = new JTextField(currJurusan);
        JComboBox<String> cbAng = new JComboBox<>(new String[]{"2022", "2023", "2024", "2025", "2026"});
        cbAng.setSelectedItem(currAngkatan);
        JTextField tfEm = new JTextField(currEmail);
        JTextField tfHp = new JTextField(currNohp);

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
            onSave.accept(new String[]{
                tfNama.getText().trim(),
                tfNim.getText().trim(),
                (String) cbFak.getSelectedItem(),
                tfJur.getText().trim(),
                (String) cbAng.getSelectedItem(),
                tfEm.getText().trim(),
                tfHp.getText().trim()
            });
        }
    }

    private JPanel createInfoItem(String labelText, JLabel valueLabel) {
        JPanel item = new JPanel();
        item.setBackground(new Color(247, 249, 252));
        item.setBorder(new EmptyBorder(16, 18, 16, 18));
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));

        JLabel lbl = UiKit.label(labelText, 13, false, new Color(127, 140, 141));
        item.add(lbl);
        item.add(Box.createVerticalStrut(7));
        item.add(valueLabel);

        return item;
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

    private void triggerMenu(String menuName) {
        Runnable r = menuActions.get(menuName);
        if (r != null) {
            r.run();
        }
    }

    public void setMenuAction(String menuName, Runnable action) {
        menuActions.put(menuName, action);
    }

    public void addEditProfileListener(ActionListener l) {
        btnEdit.addActionListener(l);
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
