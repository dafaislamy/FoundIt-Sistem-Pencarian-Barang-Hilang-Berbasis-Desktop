package com.foundit.view;

import com.foundit.model.Item;
import com.foundit.model.ItemType;
import com.foundit.service.ItemService;
import com.foundit.service.ServiceException;
import com.foundit.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class UserDashboardFrame extends JFrame {

    private final ItemService itemService = new ItemService();
    private JLabel lblLostCount, lblFoundCount, lblMyReportsCount;
    private JPanel listPanel;

    public UserDashboardFrame() {
        UiKit.frame(this, "FoundIt | Dashboard", 1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setContentPane(build());
        loadDashboardData();
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiKit.BG);
        root.add(new UserSidebarPanel(this, UserSidebarPanel.Menu.DASHBOARD), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(26, 34, 25, 34));
        main.add(UserSidebarPanel.buildTopbar("Dashboard", "Ringkasan aktivitas pelaporan barang di lingkungan kampus."), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(22, 0, 10, 8));

        JPanel welcome = UiKit.gradient(UiKit.BLUE, UiKit.BLUE_DARK);
        welcome.setLayout(new BorderLayout());
        welcome.setBorder(new EmptyBorder(22, 26, 22, 26));

        JPanel wt = new JPanel();
        wt.setOpaque(false);
        wt.setLayout(new BoxLayout(wt, BoxLayout.Y_AXIS));
        wt.add(UiKit.label("Halo, " + Session.getDisplayName() + "!", 21, true, Color.WHITE));
        wt.add(Box.createVerticalStrut(7));
        wt.add(UiKit.label("Yuk, bantu barang yang kamu temukan kembali kepada pemiliknya,", 13, false, Color.WHITE));
        wt.add(UiKit.label("dan temukan juga barangmu yang hilang melalui laporan kampus.", 13, false, Color.WHITE));
        welcome.add(wt, BorderLayout.CENTER);
        body.add(welcome);
        body.add(Box.createVerticalStrut(18));

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

        JPanel actions = UiKit.card();
        actions.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(231, 237, 243)),
                new EmptyBorder(20, 22, 20, 22)
        ));
        actions.setLayout(new BorderLayout(10, 12));
        actions.add(UiKit.header("Aksi Cepat", "Pilih jenis laporan yang ingin kamu buat."), BorderLayout.NORTH);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttons.setOpaque(false);

        JButton btnQuickLost = UiKit.button("Lapor Barang Hilang", new Color(231, 76, 60), Color.WHITE);
        btnQuickLost.setPreferredSize(new Dimension(190, 44));
        btnQuickLost.addActionListener(e -> {
            dispose();
            new LaporHilangFrame().setVisible(true);
        });

        JButton btnQuickFound = UiKit.button("Lapor Barang Ditemukan", new Color(39, 174, 96), Color.WHITE);
        btnQuickFound.setPreferredSize(new Dimension(220, 44));
        btnQuickFound.addActionListener(e -> {
            dispose();
            new LaporDitemukanFrame().setVisible(true);
        });

        JButton btnQuickSearch = UiKit.button("Cari Barang", UiKit.BLUE, Color.WHITE);
        btnQuickSearch.setPreferredSize(new Dimension(150, 44));
        btnQuickSearch.addActionListener(e -> {
            dispose();
            new CariBarangFrame().setVisible(true);
        });

        buttons.add(btnQuickLost);
        buttons.add(btnQuickFound);
        buttons.add(btnQuickSearch);
        actions.add(buttons, BorderLayout.CENTER);
        body.add(actions);
        body.add(Box.createVerticalStrut(18));

        JPanel recent = UiKit.card();
        recent.setLayout(new BorderLayout(8, 12));
        recent.add(UiKit.header("Laporan Terbaru", "Laporan barang hilang dan ditemukan di lingkungan kampus."), BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        recent.add(listPanel, BorderLayout.CENTER);
        body.add(recent);

        JScrollPane sc = new JScrollPane(body);
        sc.setBorder(null);
        sc.getViewport().setOpaque(false);
        sc.setOpaque(false);
        sc.getVerticalScrollBar().setUnitIncrement(18);

        main.add(sc, BorderLayout.CENTER);
        root.add(main, BorderLayout.CENTER);
        return root;
    }

    private void loadDashboardData() {
        try {
            List<Item> all = itemService.findAll();
            long lost = all.stream().filter(i -> i.getType() == ItemType.LOST).count();
            long found = all.stream().filter(i -> i.getType() == ItemType.FOUND).count();
            String myId = Session.getUser() != null ? Session.getUser().getId() : null;
            long mine = myId == null ? 0 : all.stream().filter(i -> myId.equals(i.getReporterId())).count();

            lblLostCount.setText(String.valueOf(lost));
            lblFoundCount.setText(String.valueOf(found));
            lblMyReportsCount.setText(String.valueOf(mine));

            listPanel.removeAll();
            int limit = Math.min(all.size(), 6);
            for (int i = 0; i < limit; i++) {
                Item item = all.get(i);
                listPanel.add(createReportRow(item));
                listPanel.add(Box.createVerticalStrut(9));
            }
            if (all.isEmpty()) {
                listPanel.add(UiKit.label("Belum ada laporan barang di database.", 13, false, UiKit.MUTED));
            }
            listPanel.revalidate();
            listPanel.repaint();
        } catch (ServiceException ex) {
            UiKit.error(this, ex.getMessage());
        }
    }

    private JPanel createReportRow(Item item) {
        boolean isLost = item.getType() == ItemType.LOST;
        JPanel row = new JPanel(new BorderLayout(15, 5));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiKit.BORDER),
                new EmptyBorder(13, 15, 13, 15)
        ));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel tag = UiKit.label(
                isLost ? "BARANG HILANG" : "BARANG DITEMUKAN",
                10, true,
                isLost ? new Color(210, 75, 65) : new Color(39, 145, 88)
        );
        info.add(tag);
        info.add(Box.createVerticalStrut(4));
        info.add(UiKit.label(item.getTitle(), 15, true, UiKit.TEXT));
        info.add(Box.createVerticalStrut(3));
        String loc = item.getLocation() != null ? item.getLocation() : "-";
        String desc = item.getDescription() != null ? item.getDescription() : "-";
        info.add(UiKit.label(loc + "  |  " + desc, 11, false, UiKit.MUTED));
        row.add(info, BorderLayout.CENTER);

        JPanel acts = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 8));
        acts.setOpaque(false);

        JButton detail = UiKit.button("Lihat Detail", UiKit.PALE, UiKit.BLUE_DARK);
        detail.setPreferredSize(new Dimension(122, 40));
        detail.addActionListener(e -> UiKit.info(this,
                "Barang: " + item.getTitle()
                        + "\nTipe: " + (item.getType() != null ? item.getType().toString() : "-")
                        + "\nStatus: " + (item.getStatus() != null ? item.getStatus().toString() : "-")
                        + "\nLokasi: " + loc
                        + "\nDeskripsi: " + desc
        ));
        acts.add(detail);

        if (!isLost) {
            JButton claim = UiKit.button("Klaim", UiKit.BLUE, Color.WHITE);
            claim.setPreferredSize(new Dimension(88, 40));
            claim.addActionListener(e -> {
                dispose();
                new KlaimFrame(item.getId(), item.getTitle()).setVisible(true);
            });
            acts.add(claim);
        }

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
}