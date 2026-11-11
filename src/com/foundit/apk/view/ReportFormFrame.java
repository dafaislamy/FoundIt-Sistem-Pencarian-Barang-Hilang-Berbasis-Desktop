package com.foundit.apk.view;

import com.foundit.apk.data.ReportRepository;
import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Form laporan memenuhi area kerja dengan sidebar navigasi lengkap.
 */
abstract class ReportFormFrame extends JFrame {

    private final String email, role, type, title;
    private JTextField name, location, date, photo;
    private JComboBox<String> category;
    private JTextArea description;

    ReportFormFrame(String email, String role, String type, String title) {
        this.email = email;
        this.role = role;
        this.type = type;
        this.title = title;

        UiKit.frame(this, "FoundIt | " + title, 1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setContentPane(build());
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiKit.BG);

        // SIDEBAR LENGKAP 6 MENU
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
        side.add(menu("Dashboard", "home", false, () -> dashboard()));
        side.add(menu(
            "Lapor Barang Hilang",
            "lost",
            type.equals("HILANG"),
            () -> {
                if (!type.equals("HILANG")) {
                    new LaporHilangFrame(email, role).setVisible(true);
                    dispose();
                }
            }
        ));
        side.add(menu(
            "Lapor Barang Ditemukan",
            "found",
            type.equals("DITEMUKAN"),
            () -> {
                if (!type.equals("DITEMUKAN")) {
                    new LaporDitemukanFrame(email, role).setVisible(true);
                    dispose();
                }
            }
        ));
        side.add(menu("Riwayat Laporan", "history", false, () -> {
            new RiwayatLaporanFrame(email, role).setVisible(true);
            dispose();
        }));
        side.add(menu("Status Klaim", "status", false, () -> {
            new StatusKlaimFrame(email, role).setVisible(true);
            dispose();
        }));

side.add(menu("Cari Barang", "search", false, () -> {
    new CariBarangFrame(email, role).setVisible(true);
    dispose();
}));
side.add(menu("Klaim Barang", "claim", false, () -> {
    new KlaimFrame(email, role).setVisible(true);
    dispose();
}));
        side.add(Box.createVerticalGlue());

        side.add(menu("Keluar", "return", false, () -> {
            new IndexFrame().setVisible(true);
            dispose();
        }));

        side.add(Box.createVerticalStrut(25));
        root.add(side, BorderLayout.WEST);

        // AREA KANAN (KONTEN + FOOTER)
        JPanel rightArea = new JPanel(new BorderLayout());
        rightArea.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.setBorder(new EmptyBorder(16, 30, 16, 30));

        JButton back = UiKit.button("Kembali ke Dashboard", UiKit.PALE, UiKit.BLUE_DARK);
        back.addActionListener(e -> dashboard());

        top.add(back, BorderLayout.WEST);
        top.add(UiKit.label("FoundIt", 23, true, UiKit.BLUE), BorderLayout.EAST);

        rightArea.add(top, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(28, 42, 30, 42));

        JPanel heading = UiKit.header(
            title,
            "Lengkapi informasi berikut dengan jelas agar laporan mudah dipahami."
        );

        content.add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(20, 0, 20, 0));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 12, 10, 12);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;

        name = UiKit.field();
        category = new JComboBox<>(new String[]{"Elektronik", "Aksesoris", "Dokumen", "Kunci", "Lainnya"});
        category.setFont(UiKit.REG);
        category.setBackground(Color.WHITE);

        form.add(UiKit.fieldBlock("Nama Barang *", name), c);

        c.gridx = 1;
        form.add(UiKit.fieldBlock("Kategori *", category), c);

        c.gridx = 0;
        c.gridy = 1;

        location = UiKit.field();
        date = UiKit.field();
        date.setText(LocalDate.now().toString());

        form.add(
            UiKit.fieldBlock(
                type.equals("HILANG") ? "Lokasi Terakhir *" : "Lokasi Ditemukan *",
                location
            ),
            c
        );

        c.gridx = 1;
        form.add(UiKit.fieldBlock("Tanggal (YYYY-MM-DD) *", date), c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;

        description = new JTextArea(7, 30);
        description.setFont(UiKit.REG);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setBorder(new EmptyBorder(10, 12, 10, 12));

        JScrollPane descScroll = new JScrollPane(description);
        descScroll.setPreferredSize(new Dimension(500, 150));
        descScroll.setBorder(BorderFactory.createLineBorder(new Color(190, 207, 222), 1, true));

        form.add(UiKit.fieldBlock("Deskripsi Barang *", descScroll), c);

        c.gridy = 3;

        photo = UiKit.field();
        photo.setEditable(false);

        JButton choose = UiKit.button("Pilih Foto", UiKit.PALE, UiKit.BLUE_DARK);
        choose.addActionListener(e -> choosePhoto());

        JPanel photoRow = new JPanel(new BorderLayout(10, 0));
        photoRow.setOpaque(false);
        photoRow.add(photo, BorderLayout.CENTER);
        photoRow.add(choose, BorderLayout.EAST);

        form.add(UiKit.fieldBlock("Foto Barang (opsional)", photoRow), c);

        content.add(form, BorderLayout.CENTER);

        JButton reset = UiKit.button("Reset Form", new Color(255, 238, 236), new Color(190, 70, 60));
        reset.addActionListener(e -> resetForm());

        JButton cancel = UiKit.button("Batal", new Color(237, 241, 245), UiKit.TEXT);
        cancel.addActionListener(e -> dashboard());

        JButton submit = UiKit.button("Kirim Laporan", UiKit.BLUE, Color.WHITE);
        submit.addActionListener(e -> submit());

        JScrollPane sc = new JScrollPane(content);
        sc.setBorder(null);
        sc.getVerticalScrollBar().setUnitIncrement(18);

        rightArea.add(sc, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UiKit.BORDER));

        footer.add(reset);
        footer.add(cancel);
        footer.add(submit);

        rightArea.add(footer, BorderLayout.SOUTH);
        root.add(rightArea, BorderLayout.CENTER);

        return root;
    }

    private JPanel menu(String text, String icon, boolean active, Runnable action) {
        JPanel p = new JPanel(new BorderLayout());
        p.setMaximumSize(new Dimension(220, 46));
        p.setBorder(new EmptyBorder(12, 18, 12, 10));
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

    private void resetForm() {
        name.setText("");
        category.setSelectedIndex(0);
        location.setText("");
        date.setText(LocalDate.now().toString());
        description.setText("");
        photo.setText("");
        name.requestFocus();
    }

    private void choosePhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih foto barang");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            photo.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void submit() {
        if (name.getText().trim().isEmpty()
                || location.getText().trim().isEmpty()
                || description.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Nama barang, lokasi, dan deskripsi wajib diisi.",
                "Data belum lengkap",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        LocalDate d;
        try {
            d = LocalDate.parse(date.getText().trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Tanggal harus menggunakan format YYYY-MM-DD.",
                "Tanggal tidak valid",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        ReportRepository.getInstance().add(
            new ReportRepository.Report(
                name.getText().trim(),
                type,
                (String) category.getSelectedItem(),
                location.getText().trim(),
                description.getText().trim(),
                d,
                photo.getText()
            )
        );

        UiKit.info(this, "Laporan berhasil ditambahkan untuk sesi aplikasi ini.");
        new RiwayatLaporanFrame(email, role).setVisible(true);
        dispose();
    }

    private void dashboard() {
        new UserDashboardFrame(email, role).setVisible(true);
        dispose();
    }
}