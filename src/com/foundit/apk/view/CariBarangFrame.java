package com.foundit.apk.view;

import com.foundit.apk.controller.CariBarangController;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentListener;

/**
 * Halaman Cari Barang dengan fitur pencarian langsung, filter status/kategori, dan kartu barang (View murni).
 */
public class CariBarangFrame extends JFrame {

    private final String email, role;
    private JTextField tfSearch;
    private JButton btnClearSearch;
    private JComboBox<String> cbStatus, cbKategori;
    private JButton btnReset;
    private JLabel lblResultCount;
    private JPanel gridWrapper;
    private final Map<String, Runnable> menuActions = new HashMap<>();

    public CariBarangFrame(String email, String role) {
        this(email, role, true);
    }

    public CariBarangFrame(String email, String role, boolean bindDefaultController) {
        this.email = email;
        this.role = role;

        UiKit.frame(this, "FoundIt | Cari Barang", 1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setContentPane(build());

        if (bindDefaultController) {
            new CariBarangController(this, email, role).bind();
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
        side.add(menu("Cari Barang", "search", true, () -> {}));
        side.add(menu("Lapor Barang Hilang", "lost", false, () -> triggerMenu("Lapor Barang Hilang")));
        side.add(menu("Lapor Barang Ditemukan", "found", false, () -> triggerMenu("Lapor Barang Ditemukan")));
        side.add(menu("Klaim Barang", "claim", false, () -> triggerMenu("Klaim Barang")));
        side.add(menu("Status Klaim", "status", false, () -> triggerMenu("Status Klaim")));
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

        // TOPBAR
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(
            UiKit.header("Daftar Barang", "Jelajahi laporan barang hilang dan ditemukan di kampus"),
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

        // BODY
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(22, 0, 10, 8));

        // 1. KOTAK PENCARIAN & FILTER
        JPanel searchBox = UiKit.card();
        searchBox.setBackground(Color.WHITE);
        searchBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(237, 240, 242)),
            new EmptyBorder(24, 26, 24, 26)
        ));
        searchBox.setLayout(new BorderLayout(0, 18));
        searchBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 155));

        JPanel searchTitle = new JPanel();
        searchTitle.setOpaque(false);
        searchTitle.setLayout(new BoxLayout(searchTitle, BoxLayout.Y_AXIS));
        searchTitle.add(UiKit.label("Cari Barang", 20, true, UiKit.TEXT));
        searchTitle.add(Box.createVerticalStrut(5));
        searchTitle.add(UiKit.label("Temukan barang berdasarkan nama, status, atau kategori.", 13, false, UiKit.MUTED));
        searchBox.add(searchTitle, BorderLayout.NORTH);

        JPanel filterRow = new JPanel(new BorderLayout(12, 0));
        filterRow.setOpaque(false);

        JPanel inputWrapper = new JPanel(new BorderLayout(6, 0));
        inputWrapper.setBackground(new Color(248, 250, 252));
        inputWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(223, 230, 233)),
            new EmptyBorder(0, 12, 0, 8)
        ));
        inputWrapper.setPreferredSize(new Dimension(320, 44));

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setForeground(new Color(127, 140, 141));
        inputWrapper.add(searchIcon, BorderLayout.WEST);

        tfSearch = new JTextField();
        tfSearch.setFont(UiKit.REG);
        tfSearch.setBorder(null);
        tfSearch.setOpaque(false);
        tfSearch.setToolTipText("Cari nama barang...");
        inputWrapper.add(tfSearch, BorderLayout.CENTER);

        btnClearSearch = new JButton("✕");
        btnClearSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClearSearch.setForeground(new Color(127, 140, 141));
        btnClearSearch.setBorderPainted(false);
        btnClearSearch.setContentAreaFilled(false);
        btnClearSearch.setFocusPainted(false);
        btnClearSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClearSearch.setVisible(false);
        inputWrapper.add(btnClearSearch, BorderLayout.EAST);

        filterRow.add(inputWrapper, BorderLayout.CENTER);

        JPanel controlsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlsRight.setOpaque(false);

        cbStatus = new JComboBox<>(new String[]{"Semua Status", "Hilang", "Ditemukan"});
        cbStatus.setFont(UiKit.REG);
        cbStatus.setBackground(Color.WHITE);
        cbStatus.setPreferredSize(new Dimension(155, 44));

        cbKategori = new JComboBox<>(new String[]{"Semua Kategori", "Elektronik", "Aksesoris", "Dokumen", "Lainnya"});
        cbKategori.setFont(UiKit.REG);
        cbKategori.setBackground(Color.WHITE);
        cbKategori.setPreferredSize(new Dimension(165, 44));

        btnReset = UiKit.button("Reset Filter", new Color(248, 250, 252), new Color(71, 85, 105));
        btnReset.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)),
            new EmptyBorder(8, 16, 8, 16)
        ));
        btnReset.setPreferredSize(new Dimension(125, 44));

        controlsRight.add(cbStatus);
        controlsRight.add(cbKategori);
        controlsRight.add(btnReset);

        filterRow.add(controlsRight, BorderLayout.EAST);
        searchBox.add(filterRow, BorderLayout.CENTER);

        body.add(searchBox);
        body.add(Box.createVerticalStrut(24));

        // 2. HEADER HASIL PENCARIAN
        JPanel resultHeader = new JPanel(new BorderLayout());
        resultHeader.setOpaque(false);
        resultHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        JPanel rhLeft = new JPanel();
        rhLeft.setOpaque(false);
        rhLeft.setLayout(new BoxLayout(rhLeft, BoxLayout.Y_AXIS));
        rhLeft.add(UiKit.label("Daftar Barang", 20, true, UiKit.TEXT));
        rhLeft.add(Box.createVerticalStrut(4));
        lblResultCount = UiKit.label("Menampilkan semua barang", 13, false, UiKit.MUTED);
        rhLeft.add(lblResultCount);

        resultHeader.add(rhLeft, BorderLayout.WEST);
        body.add(resultHeader);
        body.add(Box.createVerticalStrut(14));

        // 3. GRID KARTU BARANG
        gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        body.add(gridWrapper);

        JScrollPane sc = new JScrollPane(body);
        sc.setBorder(null);
        sc.getViewport().setOpaque(false);
        sc.setOpaque(false);
        sc.getVerticalScrollBar().setUnitIncrement(18);

        main.add(sc, BorderLayout.CENTER);
        root.add(main, BorderLayout.CENTER);

        return root;
    }

    public void renderGrid(List<String[]> hasil, Consumer<String[]> onDetail, Consumer<String[]> onClaim) {
        gridWrapper.removeAll();

        if (hasil.isEmpty()) {
            lblResultCount.setText("Tidak ada barang yang sesuai");

            JPanel noResultPanel = UiKit.card();
            noResultPanel.setBackground(Color.WHITE);
            noResultPanel.setBorder(new EmptyBorder(50, 20, 50, 20));
            noResultPanel.setLayout(new BoxLayout(noResultPanel, BoxLayout.Y_AXIS));

            JLabel icon = UiKit.label("🔎", 42, false, UiKit.TEXT);
            icon.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel title = UiKit.label("Barang tidak ditemukan", 19, true, UiKit.TEXT);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel sub = UiKit.label("Coba gunakan kata kunci atau filter yang berbeda.", 13, false, UiKit.MUTED);
            sub.setAlignmentX(Component.CENTER_ALIGNMENT);

            noResultPanel.add(icon);
            noResultPanel.add(Box.createVerticalStrut(12));
            noResultPanel.add(title);
            noResultPanel.add(Box.createVerticalStrut(6));
            noResultPanel.add(sub);

            gridWrapper.add(noResultPanel, BorderLayout.NORTH);
        } else {
            lblResultCount.setText("Menampilkan " + hasil.size() + " barang");

            int cols = 2;
            int rows = (int) Math.ceil(hasil.size() / (double) cols);
            JPanel grid = new JPanel(new GridLayout(rows, cols, 18, 18));
            grid.setOpaque(false);

            for (String[] item : hasil) {
                grid.add(createBarangCard(item, onDetail, onClaim));
            }

            if (hasil.size() % cols != 0) {
                JPanel dummy = new JPanel();
                dummy.setOpaque(false);
                grid.add(dummy);
            }

            gridWrapper.add(grid, BorderLayout.NORTH);
        }

        gridWrapper.revalidate();
        gridWrapper.repaint();
    }

    private JPanel createBarangCard(String[] item, Consumer<String[]> onDetail, Consumer<String[]> onClaim) {
        String nama = item[0];
        String status = item[1];
        String kategori = item[2];
        String deskripsi = item[3];
        boolean isHilang = "hilang".equalsIgnoreCase(status);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(237, 240, 242), 1));

        JPanel imgBox = new JPanel(new BorderLayout());
        imgBox.setBackground(new Color(241, 243, 245));
        imgBox.setPreferredSize(new Dimension(0, 140));

        String emoji = "📦";
        if (nama.toLowerCase().contains("flashdisk")) emoji = "💾";
        else if (nama.toLowerCase().contains("kunci")) emoji = "🔑";
        else if (nama.toLowerCase().contains("handphone") || nama.toLowerCase().contains("hp")) emoji = "📱";
        else if (nama.toLowerCase().contains("dompet")) emoji = "👛";

        JLabel lblEmoji = new JLabel(emoji, SwingConstants.CENTER);
        lblEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        imgBox.add(lblEmoji, BorderLayout.CENTER);
        card.add(imgBox, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(18, 20, 18, 20));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel statusBadge = new JLabel(isHilang ? "Hilang" : "Ditemukan");
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusBadge.setOpaque(true);
        statusBadge.setBorder(new EmptyBorder(4, 10, 4, 10));
        if (isHilang) {
            statusBadge.setBackground(new Color(253, 236, 234));
            statusBadge.setForeground(new Color(231, 76, 60));
        } else {
            statusBadge.setBackground(new Color(234, 250, 241));
            statusBadge.setForeground(new Color(39, 174, 96));
        }
        statusBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNama = UiKit.label(nama, 17, true, UiKit.TEXT);
        lblNama.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDesc = UiKit.label(deskripsi, 13, false, UiKit.MUTED);
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel catBadge = new JLabel(kategori);
        catBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        catBadge.setOpaque(true);
        catBadge.setBackground(new Color(241, 247, 252));
        catBadge.setForeground(UiKit.BLUE);
        catBadge.setBorder(new EmptyBorder(4, 9, 4, 9));
        catBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(statusBadge);
        content.add(Box.createVerticalStrut(10));
        content.add(lblNama);
        content.add(Box.createVerticalStrut(6));
        content.add(lblDesc);
        content.add(Box.createVerticalStrut(12));
        content.add(catBadge);
        content.add(Box.createVerticalStrut(16));

        JPanel actions = new JPanel(new GridLayout(1, isHilang ? 1 : 2, 10, 0));
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton btnDetail = UiKit.button("Lihat Detail", UiKit.BLUE, Color.WHITE);
        btnDetail.setPreferredSize(new Dimension(0, 38));
        btnDetail.addActionListener(e -> onDetail.accept(item));
        actions.add(btnDetail);

        if (!isHilang) {
            JButton btnKlaim = UiKit.button("Klaim", new Color(39, 174, 96), Color.WHITE);
            btnKlaim.setPreferredSize(new Dimension(0, 38));
            btnKlaim.addActionListener(e -> onClaim.accept(item));
            actions.add(btnKlaim);
        }

        content.add(actions);
        card.add(content, BorderLayout.CENTER);

        return card;
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

    public String getSearchKeyword() { return tfSearch.getText().trim(); }
    public void setSearchKeyword(String text) { tfSearch.setText(text); }
    public void focusSearch() { tfSearch.requestFocus(); }

    public String getSelectedStatus() { return (String) cbStatus.getSelectedItem(); }
    public void setSelectedStatusIndex(int index) { cbStatus.setSelectedIndex(index); }

    public String getSelectedKategori() { return (String) cbKategori.getSelectedItem(); }
    public void setSelectedKategoriIndex(int index) { cbKategori.setSelectedIndex(index); }

    public void setClearButtonVisible(boolean visible) { btnClearSearch.setVisible(visible); }

    public void addSearchDocumentListener(DocumentListener l) { tfSearch.getDocument().addDocumentListener(l); }
    public void addClearSearchListener(ActionListener l) { btnClearSearch.addActionListener(l); }
    public void addStatusFilterListener(ActionListener l) { cbStatus.addActionListener(l); }
    public void addCategoryFilterListener(ActionListener l) { cbKategori.addActionListener(l); }
    public void addResetFilterListener(ActionListener l) { btnReset.addActionListener(l); }
}
