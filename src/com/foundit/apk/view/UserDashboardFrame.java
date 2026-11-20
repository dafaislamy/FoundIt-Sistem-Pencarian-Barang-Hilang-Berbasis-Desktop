package com.foundit.apk.view;

import com.foundit.apk.controller.UserDashboardController;
import com.foundit.apk.model.ReportRepository;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Dashboard pengguna dengan daftar laporan yang dapat digulir dan 8 menu navigasi lengkap (View murni).
 */
public class UserDashboardFrame extends JFrame {

    private final String email, role;
    private JLabel lblLostCount, lblFoundCount, lblMyReportsCount;
    private JPanel listPanel;
    private JButton btnQuickLost, btnQuickFound, btnQuickSearch;
    private final Map<String, Runnable> menuActions = new HashMap<>();

    public UserDashboardFrame(String email, String role) {
        this(email, role, true);
    }

    public UserDashboardFrame(String email, String role, boolean bindDefaultController) {
        this.email = email;
        this.role = role;

        UiKit.frame(this, "FoundIt | Dashboard", 1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setContentPane(build());

        if (bindDefaultController) {
            new UserDashboardController(this, email, role).bind();
        }
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiKit.BG);

        // Sidebar Lengkap 8 Menu
        JPanel side = new JPanel();
        side.setBackground(Color.WHITE);
        side.setPreferredSize(new Dimension(238, 0));
        side.setBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, UiKit.BORDER)
        );
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));

        JPanel logo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logo.setOpaque(false);
        logo.setBorder(new EmptyBorder(27, 25, 32, 0));

        logo.add(UiKit.label("Found", 27, true, UiKit.BLUE_DARK));
        logo.add(UiKit.label("It", 27, true, UiKit.BLUE));

        side.add(logo);
        side.add(menu("Dashboard", "home", true, () -> triggerMenu("Dashboard")));
        side.add(menu("Cari Barang", "search", false, () -> triggerMenu("Cari Barang")));
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

        // Main Content
        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(26, 34, 25, 34));

        // Header
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        top.add(
            UiKit.header(
                "Dashboard",
                "Ringkasan aktivitas pelaporan barang di lingkungan kampus."
            ),
            BorderLayout.WEST
        );

        JPanel profile = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
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

        names.add(
            UiKit.label("Pengguna FoundIt", 13, true, UiKit.TEXT)
        );
        names.add(
            UiKit.label(email, 11, false, UiKit.MUTED)
        );

        profile.add(names);
        top.add(profile, BorderLayout.EAST);

        main.add(top, BorderLayout.NORTH);

        // Body
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(22, 0, 10, 8));

        // Welcome Section
        JPanel welcome = UiKit.gradient(UiKit.BLUE, UiKit.BLUE_DARK);
        welcome.setLayout(new BorderLayout());
        welcome.setBorder(new EmptyBorder(22, 26, 22, 26));

        JPanel wt = new JPanel();
        wt.setOpaque(false);
        wt.setLayout(new BoxLayout(wt, BoxLayout.Y_AXIS));

        wt.add(
            UiKit.label("Halo, selamat datang!", 21, true, Color.WHITE)
        );
        wt.add(Box.createVerticalStrut(7));
        wt.add(
            UiKit.label(
                "Yuk, bantu barang yang kamu temukan kembali kepada pemiliknya,",
                13, false, Color.WHITE
            )
        );
        wt.add(
            UiKit.label(
                "dan temukan juga barangmu yang hilang melalui laporan kampus.",
                13, false, Color.WHITE
            )
        );

        welcome.add(wt, BorderLayout.CENTER);
        body.add(welcome);
        body.add(Box.createVerticalStrut(18));

        // Statistics
        JPanel stats = new JPanel(new GridLayout(1, 3, 15, 0));
        stats.setOpaque(false);

        lblLostCount = UiKit.label("0", 28, true, new Color(231, 76, 60));
        lblFoundCount = UiKit.label("0", 28, true, new Color(39, 174, 96));
        lblMyReportsCount = UiKit.label("0", 28, true, UiKit.BLUE);

        stats.add(stat("Barang Hilang", lblLostCount, new Color(253, 238, 235)));
        stats.add(stat("Barang Ditemukan", lblFoundCount, new Color(232, 248, 239)));
        stats.add(stat("Laporan Saya", lblMyReportsCount, UiKit.PALE));

        body.add(stats);
        body.add(Box.createVerticalStrut(18));

        // Quick Actions
        JPanel actions = UiKit.card();
        actions.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(231, 237, 243)
                ),
                new EmptyBorder(20, 0, 20, 22)
            )
        );
        actions.setLayout(new BorderLayout(10, 12));

        actions.add(
            UiKit.header(
                "Aksi Cepat",
                "Pilih jenis laporan yang ingin kamu buat."
            ),
            BorderLayout.NORTH
        );

        JPanel buttons = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 12, 0)
        );
        buttons.setOpaque(false);

        btnQuickLost = UiKit.button(
            "Lapor Barang Hilang",
            new Color(231, 76, 60),
            Color.WHITE
        );
        spaceButton(btnQuickLost, 190, 44);

        btnQuickFound = UiKit.button(
            "Lapor Barang Ditemukan",
            new Color(39, 174, 96),
            Color.WHITE
        );
        spaceButton(btnQuickFound, 220, 44);

        btnQuickSearch = UiKit.button(
            "Cari Barang",
            UiKit.BLUE,
            Color.WHITE
        );
        spaceButton(btnQuickSearch, 150, 44);

        buttons.add(btnQuickLost);
        buttons.add(btnQuickFound);
        buttons.add(btnQuickSearch);

        actions.add(buttons, BorderLayout.CENTER);
        body.add(actions);
        body.add(Box.createVerticalStrut(18));

        // Recent Reports
        JPanel recent = UiKit.card();
        recent.setLayout(new BorderLayout(8, 12));

        recent.add(
            UiKit.header(
                "Laporan Terbaru",
                "laporan barang hilang dan ditemukan di lingkungan kampus."
            ),
            BorderLayout.NORTH
        );

        listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        recent.add(listPanel, BorderLayout.CENTER);
        body.add(recent);

        // Scroll Panel
        JScrollPane sc = new JScrollPane(body);
        sc.setBorder(null);
        sc.getViewport().setOpaque(false);
        sc.setOpaque(false);
        sc.getVerticalScrollBar().setUnitIncrement(18);

        main.add(sc, BorderLayout.CENTER);
        root.add(main, BorderLayout.CENTER);

        return root;
    }

    public void updateStats(long lostCount, long foundCount, int myReportsCount) {
        lblLostCount.setText(String.valueOf(lostCount));
        lblFoundCount.setText(String.valueOf(foundCount));
        lblMyReportsCount.setText(String.valueOf(myReportsCount));
    }

    public void renderReports(List<ReportRepository.Report> reports, Consumer<ReportRepository.Report> onDetail, Consumer<ReportRepository.Report> onClaim) {
        listPanel.removeAll();
        for (ReportRepository.Report r : reports) {
            listPanel.add(createReportRow(r.getName(), r.getType(), r.getLocation(), r.getDescription(),
                () -> onDetail.accept(r),
                () -> onClaim.accept(r)
            ));
            listPanel.add(Box.createVerticalStrut(9));
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    public void renderSampleReports(List<String[]> samples, Consumer<String[]> onDetail, Consumer<String[]> onClaim) {
        listPanel.removeAll();
        for (String[] s : samples) {
            // s = [name, type, location, description]
            listPanel.add(createReportRow(s[0], s[1], s[2], s[3],
                () -> onDetail.accept(s),
                () -> onClaim.accept(s)
            ));
            listPanel.add(Box.createVerticalStrut(9));
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createReportRow(String name, String type, String loc, String desc, Runnable onDetail, Runnable onClaim) {
        JPanel row = new JPanel(new BorderLayout(15, 5));
        row.setBackground(Color.WHITE);
        row.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiKit.BORDER),
                new EmptyBorder(13, 15, 13, 15)
            )
        );

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel tag = UiKit.label(
            type.equalsIgnoreCase("HILANG") ? "BARANG HILANG" : "BARANG DITEMUKAN",
            10,
            true,
            type.equalsIgnoreCase("HILANG") ? new Color(210, 75, 65) : new Color(39, 145, 88)
        );

        info.add(tag);
        info.add(Box.createVerticalStrut(4));
        info.add(UiKit.label(name, 15, true, UiKit.TEXT));
        info.add(Box.createVerticalStrut(3));
        info.add(UiKit.label(loc + "  |  " + desc, 11, false, UiKit.MUTED));

        row.add(info, BorderLayout.CENTER);

        JPanel acts = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 8));
        acts.setOpaque(false);

        JButton detail = UiKit.button("Lihat Detail", UiKit.PALE, UiKit.BLUE_DARK);
        spaceButton(detail, 122, 40);
        detail.addActionListener(e -> onDetail.run());

        JButton claim = UiKit.button("Klaim", UiKit.BLUE, Color.WHITE);
        spaceButton(claim, 88, 40);
        claim.addActionListener(e -> onClaim.run());

        acts.add(detail);
        acts.add(claim);

        row.add(acts, BorderLayout.EAST);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        return row;
    }

    private JPanel stat(String title, JLabel valueLabel, Color bg) {
        JPanel p = UiKit.card();
        p.setBackground(bg);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(UiKit.label(title, 12, true, UiKit.MUTED));
        p.add(Box.createVerticalStrut(8));
        p.add(valueLabel);

        return p;
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

    public void addQuickLostListener(ActionListener l) { btnQuickLost.addActionListener(l); }
    public void addQuickFoundListener(ActionListener l) { btnQuickFound.addActionListener(l); }
    public void addQuickSearchListener(ActionListener l) { btnQuickSearch.addActionListener(l); }

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
