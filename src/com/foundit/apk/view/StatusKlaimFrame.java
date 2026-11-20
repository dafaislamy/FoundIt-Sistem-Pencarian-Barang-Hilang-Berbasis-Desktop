package com.foundit.apk.view;

import com.foundit.apk.controller.StatusKlaimController;
import com.foundit.apk.model.ClaimRepository;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Halaman untuk memantau status pengajuan klaim barang pengguna (View murni).
 */
public class StatusKlaimFrame extends JFrame {

    private final String email, role;
    private JPanel klaimListPanel;
    private JLabel jumlahKlaimLabel;
    private JButton btnAjukan;
    private final Map<String, Runnable> menuActions = new HashMap<>();

    public StatusKlaimFrame(String email, String role) {
        this(email, role, true);
    }

    public StatusKlaimFrame(String email, String role, boolean bindDefaultController) {
        this.email = email;
        this.role = role;

        UiKit.frame(this, "FoundIt | Status Klaim", 1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setContentPane(build());

        if (bindDefaultController) {
            new StatusKlaimController(this, email, role).bind();
        }
    }

    /**
     * Delegasi ke ClaimRepository untuk menjaga kompatibilitas.
     */
    public static void tambahKlaim(String namaBarang, String kategori, String lokasi, String ciri, String keterangan) {
        ClaimRepository.tambahKlaim(namaBarang, kategori, lokasi, ciri, keterangan);
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
        side.add(menu("Status Klaim", "status", true, () -> {}));
        side.add(menu("Riwayat Laporan", "history", false, () -> triggerMenu("Riwayat Laporan")));
        side.add(menu("Profil", "profile", false, () -> triggerMenu("Profil")));
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
            UiKit.header("Status Klaim", "Pantau status pengajuan klaim barang kamu."),
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

        JPanel statusHeader = new JPanel(new BorderLayout());
        statusHeader.setOpaque(false);
        statusHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(237, 240, 242)),
            new EmptyBorder(0, 0, 20, 0)
        ));

        JPanel headerLeft = new JPanel();
        headerLeft.setOpaque(false);
        headerLeft.setLayout(new BoxLayout(headerLeft, BoxLayout.Y_AXIS));
        headerLeft.add(UiKit.label("Pengajuan Klaim", 20, true, UiKit.TEXT));
        headerLeft.add(Box.createVerticalStrut(5));
        jumlahKlaimLabel = UiKit.label("Memuat data...", 13, false, UiKit.MUTED);
        headerLeft.add(jumlahKlaimLabel);

        statusHeader.add(headerLeft, BorderLayout.WEST);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        headerRight.setOpaque(false);
        btnAjukan = UiKit.button("+ Ajukan Klaim", UiKit.BLUE, Color.WHITE);
        spaceButton(btnAjukan, 155, 42);
        headerRight.add(btnAjukan);

        statusHeader.add(headerRight, BorderLayout.EAST);
        container.add(statusHeader, BorderLayout.NORTH);

        klaimListPanel = new JPanel();
        klaimListPanel.setOpaque(false);
        klaimListPanel.setLayout(new BoxLayout(klaimListPanel, BoxLayout.Y_AXIS));

        container.add(klaimListPanel, BorderLayout.CENTER);
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

    public void setJumlahKlaimText(String text) {
        jumlahKlaimLabel.setText(text);
    }

    public void renderKlaimList(List<String[]> daftarKlaim, Runnable onAjukanKlaim) {
        klaimListPanel.removeAll();

        if (daftarKlaim.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(new Color(248, 250, 252));
            emptyPanel.setBorder(new EmptyBorder(50, 20, 50, 20));
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));

            JLabel emptyTitle = UiKit.label("Belum Ada Pengajuan Klaim", 18, true, UiKit.TEXT);
            emptyTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel emptySub = UiKit.label("Kamu belum mengajukan klaim barang.", 13, false, UiKit.MUTED);
            emptySub.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton btnEmpty = UiKit.button("Ajukan Klaim", UiKit.BLUE, Color.WHITE);
            spaceButton(btnEmpty, 145, 40);
            btnEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnEmpty.addActionListener(e -> onAjukanKlaim.run());

            emptyPanel.add(emptyTitle);
            emptyPanel.add(Box.createVerticalStrut(8));
            emptyPanel.add(emptySub);
            emptyPanel.add(Box.createVerticalStrut(20));
            emptyPanel.add(btnEmpty);

            klaimListPanel.add(emptyPanel);
        } else {
            for (int i = daftarKlaim.size() - 1; i >= 0; i--) {
                String[] k = daftarKlaim.get(i);
                klaimListPanel.add(createClaimCard(k));
                if (i > 0) {
                    klaimListPanel.add(Box.createVerticalStrut(15));
                }
            }
        }

        klaimListPanel.revalidate();
        klaimListPanel.repaint();
    }

    private JPanel createClaimCard(String[] klaim) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(237, 240, 242), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel top = new JPanel(new BorderLayout(15, 0));
        top.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(UiKit.label(klaim[0], 17, true, UiKit.TEXT));
        titleBox.add(Box.createVerticalStrut(5));
        titleBox.add(UiKit.label("Pengajuan klaim barang", 12, false, UiKit.MUTED));

        top.add(titleBox, BorderLayout.WEST);

        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgeWrap.setOpaque(false);

        JLabel badge = new JLabel(klaim[1]);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(6, 12, 6, 12));

        if (klaim[1].toLowerCase().contains("setuju")) {
            badge.setBackground(new Color(234, 248, 239));
            badge.setForeground(new Color(39, 174, 96));
        } else {
            badge.setBackground(new Color(255, 246, 223));
            badge.setForeground(new Color(214, 137, 16));
        }

        badgeWrap.add(badge);
        top.add(badgeWrap, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        JPanel detailGrid = new JPanel(new GridLayout(2, 2, 12, 12));
        detailGrid.setOpaque(false);
        detailGrid.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(240, 242, 243)),
            new EmptyBorder(15, 0, 0, 0)
        ));

        detailGrid.add(createDetailItem("Kategori", klaim[2]));
        detailGrid.add(createDetailItem("Lokasi Kehilangan", klaim[3]));
        detailGrid.add(createDetailItem("Ciri Barang", klaim[4]));
        detailGrid.add(createDetailItem("Keterangan", klaim[5]));

        card.add(detailGrid, BorderLayout.CENTER);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));

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

    public void addAjukanKlaimListener(ActionListener l) {
        btnAjukan.addActionListener(l);
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
}
