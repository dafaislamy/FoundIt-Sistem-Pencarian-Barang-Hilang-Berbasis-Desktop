package com.foundit.apk.view;

import com.foundit.apk.data.ReportRepository;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Halaman untuk melihat riwayat laporan barang yang pernah dibuat pengguna.
 */
public class RiwayatLaporanFrame extends JFrame {

    private final String email, role;
    private final ReportRepository repo = ReportRepository.getInstance();

    private JPanel riwayatListPanel;
    private JLabel jumlahLaporanLabel;

    public RiwayatLaporanFrame(String email, String role) {
        this.email = email;
        this.role = role;

        UiKit.frame(this, "FoundIt | Riwayat Laporan", 1200, 760);
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
        side.add(menu("Dashboard", "home", false, () -> openDashboard()));
        side.add(menu("Lapor Barang Hilang", "lost", false, () -> openLost()));
        side.add(menu("Lapor Barang Ditemukan", "found", false, () -> openFound()));
        side.add(menu("Riwayat Laporan", "history", true, () -> {}));
        side.add(menu("Status Klaim", "status", false, () -> openStatusKlaim()));
        side.add(menu("Lengkapi Profil", "profile", false, () -> openRegister()));

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
            UiKit.header("Riwayat Laporan", "Lihat laporan barang yang pernah kamu buat."),
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

        JPanel container = UiKit.card();
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(237, 240, 242)),
            new EmptyBorder(28, 28, 28, 28)
        ));
        container.setLayout(new BorderLayout(0, 22));

        JPanel historyHeader = new JPanel(new BorderLayout());
        historyHeader.setOpaque(false);
        historyHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(237, 240, 242)),
            new EmptyBorder(0, 0, 20, 0)
        ));

        JPanel headerLeft = new JPanel();
        headerLeft.setOpaque(false);
        headerLeft.setLayout(new BoxLayout(headerLeft, BoxLayout.Y_AXIS));
        headerLeft.add(UiKit.label("Daftar Laporan", 20, true, UiKit.TEXT));
        headerLeft.add(Box.createVerticalStrut(5));
        jumlahLaporanLabel = UiKit.label("Memuat laporan...", 13, false, UiKit.MUTED);
        headerLeft.add(jumlahLaporanLabel);

        historyHeader.add(headerLeft, BorderLayout.WEST);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        headerRight.setOpaque(false);
        JButton btnBuat = UiKit.button("+ Buat Laporan", UiKit.BLUE, Color.WHITE);
        spaceButton(btnBuat, 155, 42);
        btnBuat.addActionListener(e -> openLost());
        headerRight.add(btnBuat);

        historyHeader.add(headerRight, BorderLayout.EAST);
        container.add(historyHeader, BorderLayout.NORTH);

        riwayatListPanel = new JPanel();
        riwayatListPanel.setOpaque(false);
        riwayatListPanel.setLayout(new BoxLayout(riwayatListPanel, BoxLayout.Y_AXIS));

        refreshRiwayatList();

        container.add(riwayatListPanel, BorderLayout.CENTER);
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

    private void refreshRiwayatList() {
        riwayatListPanel.removeAll();
        List<ReportRepository.Report> daftarLaporan = repo.findAll();

        jumlahLaporanLabel.setText(daftarLaporan.size() + " laporan ditemukan");

        if (daftarLaporan.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(new Color(248, 250, 252));
            emptyPanel.setBorder(new EmptyBorder(50, 20, 50, 20));
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));

            JLabel emptyTitle = UiKit.label("Belum Ada Laporan", 18, true, UiKit.TEXT);
            emptyTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel emptySub = UiKit.label("Kamu belum membuat laporan barang.", 13, false, UiKit.MUTED);
            emptySub.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton btnEmpty = UiKit.button("Buat Laporan", UiKit.BLUE, Color.WHITE);
            spaceButton(btnEmpty, 145, 40);
            btnEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnEmpty.addActionListener(e -> openLost());

            emptyPanel.add(emptyTitle);
            emptyPanel.add(Box.createVerticalStrut(8));
            emptyPanel.add(emptySub);
            emptyPanel.add(Box.createVerticalStrut(20));
            emptyPanel.add(btnEmpty);

            riwayatListPanel.add(emptyPanel);
        } else {
            for (int i = daftarLaporan.size() - 1; i >= 0; i--) {
                ReportRepository.Report r = daftarLaporan.get(i);
                riwayatListPanel.add(createHistoryCard(r));
                if (i > 0) {
                    riwayatListPanel.add(Box.createVerticalStrut(15));
                }
            }
        }

        riwayatListPanel.revalidate();
        riwayatListPanel.repaint();
    }

    private JPanel createHistoryCard(ReportRepository.Report r) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(237, 240, 242), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel top = new JPanel(new BorderLayout(15, 0));
        top.setOpaque(false);

        boolean isHilang = "HILANG".equalsIgnoreCase(r.getType());
        String jenisLaporan = isHilang ? "Barang Hilang" : "Barang Ditemukan";
        String statusText = isHilang ? "Hilang" : "Ditemukan";

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(UiKit.label(r.getName(), 17, true, UiKit.TEXT));
        titleBox.add(Box.createVerticalStrut(5));
        titleBox.add(UiKit.label(jenisLaporan, 12, false, UiKit.MUTED));

        top.add(titleBox, BorderLayout.WEST);

        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgeWrap.setOpaque(false);

        JLabel badge = new JLabel(statusText);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(6, 11, 6, 11));

        if (isHilang) {
            badge.setBackground(new Color(253, 236, 234));
            badge.setForeground(new Color(231, 76, 60));
        } else {
            badge.setBackground(new Color(234, 248, 239));
            badge.setForeground(new Color(39, 174, 96));
        }

        badgeWrap.add(badge);
        top.add(badgeWrap, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        JPanel detailGrid = new JPanel(new GridLayout(1, 3, 12, 0));
        detailGrid.setOpaque(false);
        detailGrid.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(240, 242, 243)),
            new EmptyBorder(15, 0, 0, 0)
        ));

        String kategoriVal = (r.getCategory() != null && !r.getCategory().isEmpty()) ? r.getCategory() : "Umum";
        String tanggalVal = (r.getDate() != null) ? r.getDate().toString() : "-";

        detailGrid.add(createDetailItem("Kategori", kategoriVal));
        detailGrid.add(createDetailItem("Lokasi", r.getLocation()));
        detailGrid.add(createDetailItem("Tanggal", tanggalVal));

        card.add(detailGrid, BorderLayout.CENTER);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 175));

        return card;
    }

    private JPanel createDetailItem(String label, String value) {
        JPanel item = new JPanel();
        item.setBackground(new Color(248, 250, 252));
        item.setBorder(new EmptyBorder(12, 14, 12, 14));
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));

        JLabel lbl = UiKit.label(label, 11, false, new Color(149, 165, 166));
        JLabel val = UiKit.label(value, 13, true, UiKit.TEXT);

        item.add(lbl);
        item.add(Box.createVerticalStrut(5));
        item.add(val);

        return item;
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

    private void spaceButton(JButton button, int width, int height) {
        button.setMargin(new Insets(8, 24, 8, 24));
        button.setBorder(new EmptyBorder(8, 24, 8, 24));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setPreferredSize(new Dimension(width, height));
        button.setMinimumSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
    }

    private void openDashboard() {
        new UserDashboardFrame(email, role).setVisible(true);
        dispose();
    }

    private void openLost() {
        new LaporHilangFrame(email, role).setVisible(true);
        dispose();
    }

    private void openFound() {
        new LaporDitemukanFrame(email, role).setVisible(true);
        dispose();
    }

    private void openStatusKlaim() {
        new StatusKlaimFrame(email, role).setVisible(true);
        dispose();
    }

    private void openRegister() {
        new RegisterFrame(email, role).setVisible(true);
        dispose();
    }
}