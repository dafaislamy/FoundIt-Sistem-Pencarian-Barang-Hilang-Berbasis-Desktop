package com.foundit.apk.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Halaman Form Pengajuan Klaim Barang (Konversi dari klaim.html dan klaim.js).
 */
public class KlaimFrame extends JFrame {

    private final String email, role;
    private JTextField tfNamaBarang, tfLokasiHilang;
    private JComboBox<String> cbKategori;
    private JTextArea taCiriBarang, taKeterangan;

    private final String prefillNama, prefillKategori, prefillLokasi;

    public KlaimFrame(String email, String role) {
        this(email, role, "", "", "");
    }

    // Konstruktor dengan prefill otomatis saat tombol "Klaim" diklik dari halaman Cari Barang / Dashboard
    public KlaimFrame(String email, String role, String prefillNama, String prefillKategori, String prefillLokasi) {
        this.email = email;
        this.role = role;
        this.prefillNama = prefillNama;
        this.prefillKategori = prefillKategori;
        this.prefillLokasi = prefillLokasi;

        UiKit.frame(this, "FoundIt | Klaim Barang", 1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setContentPane(build());
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiKit.BG);

        // SIDEBAR LENGKAP
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
        side.add(menu("Klaim Barang", "claim", true, () -> {}));
        side.add(menu("Status Klaim", "status", false, () -> {
            new StatusKlaimFrame(email, role).setVisible(true);
            dispose();
        }));
        side.add(menu("Riwayat Laporan", "history", false, () -> {
            new RiwayatLaporanFrame(email, role).setVisible(true);
            dispose();
        }));

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

        // TOPBAR
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(
            UiKit.header("Klaim Barang", "Ajukan klaim untuk barang yang kamu kenali."),
            BorderLayout.WEST
        );

        JPanel profile = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        profile.setOpaque(false);

        String initial = (email != null && !email.isEmpty()) ? email.substring(0, 1).toUpperCase() : "U";
        JLabel avatar = UiKit.label(initial, 16, true, Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(UiKit.BLUE);
        avatar.setPreferredSize(new Dimension(40, 40));
        profile.add(avatar);

        JPanel names = new JPanel();
        names.setOpaque(false);
        names.setLayout(new BoxLayout(names, BoxLayout.Y_AXIS));
        names.add(UiKit.label("Pengguna FoundIt", 13, true, UiKit.TEXT));
        names.add(UiKit.label(email, 11, false, UiKit.MUTED));
        profile.add(names);

        top.add(profile, BorderLayout.EAST);
        main.add(top, BorderLayout.NORTH);

        // BODY CONTENT
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(22, 0, 10, 8));

        // CONTAINER FORM KLAIM (.claim-container)
        JPanel container = UiKit.card();
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(237, 240, 242)),
            new EmptyBorder(28, 30, 28, 30)
        ));
        container.setLayout(new BorderLayout(0, 22));

        // Header Form (.claim-header)
        JPanel claimHeader = new JPanel();
        claimHeader.setOpaque(false);
        claimHeader.setLayout(new BoxLayout(claimHeader, BoxLayout.Y_AXIS));
        claimHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(237, 240, 242)),
            new EmptyBorder(0, 0, 18, 0)
        ));
        claimHeader.add(UiKit.label("Form Pengajuan Klaim", 20, true, UiKit.TEXT));
        claimHeader.add(Box.createVerticalStrut(6));
        claimHeader.add(UiKit.label("Isi informasi berikut untuk membantu proses verifikasi kepemilikan barang.", 13, false, UiKit.MUTED));

        container.add(claimHeader, BorderLayout.NORTH);

        // Form Fields (#klaimForm)
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        // 1. Nama Barang
        tfNamaBarang = UiKit.field();
        tfNamaBarang.setText(prefillNama);
        formPanel.add(UiKit.fieldBlock("Nama Barang *", tfNamaBarang));
        formPanel.add(Box.createVerticalStrut(16));

        // 2. Kategori Barang
        cbKategori = new JComboBox<>(new String[]{
            "Pilih kategori", "Elektronik", "Aksesoris", "Dokumen", "Lainnya"
        });
        cbKategori.setFont(UiKit.REG);
        cbKategori.setBackground(Color.WHITE);
        if (!prefillKategori.isEmpty()) {
            for (int i = 0; i < cbKategori.getItemCount(); i++) {
                if (cbKategori.getItemAt(i).equalsIgnoreCase(prefillKategori)) {
                    cbKategori.setSelectedIndex(i);
                    break;
                }
            }
        }
        formPanel.add(UiKit.fieldBlock("Kategori Barang *", cbKategori));
        formPanel.add(Box.createVerticalStrut(16));

        // 3. Ciri-ciri Barang
        taCiriBarang = new JTextArea(4, 30);
        taCiriBarang.setFont(UiKit.REG);
        taCiriBarang.setLineWrap(true);
        taCiriBarang.setWrapStyleWord(true);
        taCiriBarang.setBorder(new EmptyBorder(10, 12, 10, 12));
        JScrollPane scrollCiri = new JScrollPane(taCiriBarang);
        scrollCiri.setPreferredSize(new Dimension(500, 110));
        scrollCiri.setBorder(BorderFactory.createLineBorder(new Color(223, 230, 233), 1, true));
        formPanel.add(UiKit.fieldBlock("Ciri-ciri Barang *", scrollCiri));
        formPanel.add(Box.createVerticalStrut(16));

        // 4. Lokasi Kehilangan
        tfLokasiHilang = UiKit.field();
        tfLokasiHilang.setText(prefillLokasi);
        formPanel.add(UiKit.fieldBlock("Lokasi Kehilangan *", tfLokasiHilang));
        formPanel.add(Box.createVerticalStrut(16));

        // 5. Keterangan Tambahan
        taKeterangan = new JTextArea(4, 30);
        taKeterangan.setFont(UiKit.REG);
        taKeterangan.setLineWrap(true);
        taKeterangan.setWrapStyleWord(true);
        taKeterangan.setBorder(new EmptyBorder(10, 12, 10, 12));
        JScrollPane scrollKet = new JScrollPane(taKeterangan);
        scrollKet.setPreferredSize(new Dimension(500, 95));
        scrollKet.setBorder(BorderFactory.createLineBorder(new Color(223, 230, 233), 1, true));
        formPanel.add(UiKit.fieldBlock("Keterangan Tambahan *", scrollKet));

        container.add(formPanel, BorderLayout.CENTER);

        // Tombol Batal & Ajukan Klaim (.form-actions)
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(237, 240, 242)),
            new EmptyBorder(20, 0, 0, 0)
        ));

        JButton btnCancel = UiKit.button("Batal", new Color(236, 240, 241), UiKit.TEXT);
        spaceButton(btnCancel, 110, 42);
        btnCancel.addActionListener(e -> {
            new CariBarangFrame(email, role).setVisible(true);
            dispose();
        });

        JButton btnSubmit = UiKit.button("Ajukan Klaim", UiKit.BLUE, Color.WHITE);
        spaceButton(btnSubmit, 150, 42);
        btnSubmit.addActionListener(e -> submitKlaim());

        actions.add(btnCancel);
        actions.add(btnSubmit);

        container.add(actions, BorderLayout.SOUTH);
        body.add(container);

        JScrollPane sc = new JScrollPane(body);
        sc.setBorder(null);
        sc.getViewport().setOpaque(false);
        sc.setOpaque(false);
        sc.getVerticalScrollBar().setUnitIncrement(18);

        main.add(sc, BorderLayout.CENTER);
        root.add(main, BorderLayout.CENTER);

        return root;
    }

    private void submitKlaim() {
        String nama = tfNamaBarang.getText().trim();
        String kategori = (String) cbKategori.getSelectedItem();
        String ciri = taCiriBarang.getText().trim();
        String lokasi = tfLokasiHilang.getText().trim();
        String keterangan = taKeterangan.getText().trim();

        if (nama.isEmpty() || cbKategori.getSelectedIndex() == 0 || ciri.isEmpty() || lokasi.isEmpty() || keterangan.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Harap lengkapi semua kolom pada form pengajuan klaim!",
                "Data Belum Lengkap",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Menyimpan ke daftarKlaim dan mengarahkan ke StatusKlaimFrame (sesuai klaim.js)
        StatusKlaimFrame.tambahKlaim(nama, kategori, lokasi, ciri, keterangan);

        JOptionPane.showMessageDialog(
            this,
            "Pengajuan klaim berhasil dikirim!",
            "Berhasil",
            JOptionPane.INFORMATION_MESSAGE
        );

        new StatusKlaimFrame(email, role).setVisible(true);
        dispose();
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