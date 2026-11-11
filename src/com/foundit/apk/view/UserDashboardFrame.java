package com.foundit.apk.view;

import com.foundit.apk.data.ReportRepository;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Dashboard pengguna dengan daftar laporan yang dapat digulir dan 8 menu navigasi lengkap.
 */
public class UserDashboardFrame extends JFrame {

    private final String email, role;
    private final ReportRepository repo = ReportRepository.getInstance();

    public UserDashboardFrame(String email, String role) {
        this.email = email;
        this.role = role;

        UiKit.frame(this, "FoundIt | Dashboard", 1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setContentPane(build());
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiKit.BG);

        // Sidebar Lengkap 8 Menu Sesuai Urutan HTML
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
        side.add(menu("Dashboard", "home", true, () -> {}));
        side.add(menu("Cari Barang", "search", false, () -> openCariBarang()));
        side.add(menu("Lapor Barang Hilang", "lost", false, () -> openLost()));
        side.add(menu("Lapor Barang Ditemukan", "found", false, () -> openFound()));
        side.add(menu("Klaim Barang", "claim", false, () -> openKlaim()));
        side.add(menu("Status Klaim", "status", false, () -> openStatusKlaim()));
        side.add(menu("Riwayat Laporan", "history", false, () -> openRiwayatLaporan()));
        side.add(menu("Profil", "profile", false, () -> openProfil()));

        side.add(Box.createVerticalGlue());

        side.add(menu(
            "Keluar",
            "return",
            false,
            () -> {
                new IndexFrame().setVisible(true);
                dispose();
            }
        ));

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

        stats.add(
            stat(
                "Barang Hilang",
                String.valueOf(repo.count("HILANG")),
                new Color(253, 238, 235),
                new Color(231, 76, 60)
            )
        );

        stats.add(
            stat(
                "Barang Ditemukan",
                String.valueOf(repo.count("DITEMUKAN")),
                new Color(232, 248, 239),
                new Color(39, 174, 96)
            )
        );

        stats.add(
            stat(
                "Laporan Saya",
                String.valueOf(repo.findAll().size()),
                UiKit.PALE,
                UiKit.BLUE
            )
        );

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

        JButton lost = UiKit.button(
            "Lapor Barang Hilang",
            new Color(231, 76, 60),
            Color.WHITE
        );

        spaceButton(lost, 190, 44);
        lost.addActionListener(e -> openLost());

        JButton found = UiKit.button(
            "Lapor Barang Ditemukan",
            new Color(39, 174, 96),
            Color.WHITE
        );

        spaceButton(found, 220, 44);
        found.addActionListener(e -> openFound());

        JButton searchBtn = UiKit.button(
            "Cari Barang",
            UiKit.BLUE,
            Color.WHITE
        );
        spaceButton(searchBtn, 150, 44);
        searchBtn.addActionListener(e -> openCariBarang());

        buttons.add(lost);
        buttons.add(found);
        buttons.add(searchBtn);

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

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        if (repo.findAll().isEmpty()) {
            addSample(
                list,
                "Dompet kulit warna hitam",
                "HILANG",
                "Integrated Classroom",
                "Dompet hitam kecil dengan resleting dan kartu identitas"
            );

            addSample(
                list,
                "Tumbler biru 600 ml",
                "DITEMUKAN",
                "Kantin FMIPA",
                "Tumbler tertinggal di meja pojok kanan"
            );

            addSample(
                list,
                "Flashdisk SanDisk 64GB",
                "HILANG",
                "Lab RPL",
                "Flashdisk warna hitam dengan gantungan merah."
            );

            addSample(
                list,
                "Kunci motor dengan gantungan",
                "DITEMUKAN",
                "Area Parkir IL",
                "Ditemukan dekat parkiran depan IL"
            );

        } else {
            for (ReportRepository.Report r : repo.findAll()) {
                addReport(
                    list,
                    r.getName(),
                    r.getType(),
                    r.getLocation(),
                    r.getDescription()
                );
            }
        }

        recent.add(list, BorderLayout.CENTER);
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

    private void addSample(
            JPanel list,
            String name,
            String type,
            String loc,
            String desc) {

        addReport(list, name, type, loc, desc);
    }

    private void addReport(
            JPanel list,
            String name,
            String type,
            String loc,
            String desc) {

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
            type.equals("HILANG")
                ? "BARANG HILANG"
                : "BARANG DITEMUKAN",
            10,
            true,
            type.equals("HILANG")
                ? new Color(210, 75, 65)
                : new Color(39, 145, 88)
        );

        info.add(tag);
        info.add(Box.createVerticalStrut(4));
        info.add(UiKit.label(name, 15, true, UiKit.TEXT));
        info.add(Box.createVerticalStrut(3));
        info.add(
            UiKit.label(loc + "  |  " + desc, 11, false, UiKit.MUTED)
        );

        row.add(info, BorderLayout.CENTER);

        JPanel acts = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 7, 8)
        );
        acts.setOpaque(false);

        JButton detail = UiKit.button(
            "Lihat Detail",
            UiKit.PALE,
            UiKit.BLUE_DARK
        );

        spaceButton(detail, 122, 40);

        detail.addActionListener(e ->
            JOptionPane.showMessageDialog(
                this,
                "Nama: " + name
                    + "\nStatus: " + type
                    + "\nLokasi: " + loc
                    + "\nDeskripsi: " + desc,
                "Detail Barang",
                JOptionPane.INFORMATION_MESSAGE
            )
        );

        JButton claim = UiKit.button(
            "Klaim",
            UiKit.BLUE,
            Color.WHITE
        );

        spaceButton(claim, 88, 40);

        claim.addActionListener(e -> {
            new KlaimFrame(email, role, name, "Lainnya", loc).setVisible(true);
            dispose();
        });

        acts.add(detail);
        acts.add(claim);

        row.add(acts, BorderLayout.EAST);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        list.add(row);
        list.add(Box.createVerticalStrut(9));
    }

    private JPanel menu(
            String text,
            String icon,
            boolean active,
            Runnable action) {

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

    private JPanel stat(
            String title,
            String value,
            Color bg,
            Color accent) {

        JPanel p = UiKit.card();

        p.setBackground(bg);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(UiKit.label(title, 12, true, UiKit.MUTED));
        p.add(Box.createVerticalStrut(8));
        p.add(UiKit.label(value, 28, true, accent));

        return p;
    }

    private void spaceButton(
            JButton button,
            int width,
            int height) {

        button.setMargin(new Insets(8, 24, 8, 24));
        button.setBorder(new EmptyBorder(8, 24, 8, 24));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setPreferredSize(new Dimension(width, height));
        button.setMinimumSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
    }

    private void openCariBarang() {
        new CariBarangFrame(email, role).setVisible(true);
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

    private void openKlaim() {
        new KlaimFrame(email, role).setVisible(true);
        dispose();
    }

    private void openStatusKlaim() {
        new StatusKlaimFrame(email, role).setVisible(true);
        dispose();
    }

    private void openRiwayatLaporan() {
        new RiwayatLaporanFrame(email, role).setVisible(true);
        dispose();
    }

    private void openProfil() {
        new ProfilFrame(email, role).setVisible(true);
        dispose();
    }
}