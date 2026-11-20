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

/**
 * Halaman riwayat laporan barang pengguna terhubung ke ItemService (Database).
 */
public class RiwayatLaporanFrame extends JFrame {

    private final ItemService itemService = new ItemService();
    private JPanel riwayatListPanel;
    private JLabel jumlahLaporanLabel;

    public RiwayatLaporanFrame() {
        UiKit.frame(this, "FoundIt | Riwayat Laporan", 1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setContentPane(build());
        loadHistory();
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiKit.BG);
        root.add(new UserSidebarPanel(this, UserSidebarPanel.Menu.RIWAYAT_LAPORAN), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(26, 34, 25, 34));
        main.add(UserSidebarPanel.buildTopbar("Riwayat Laporan", "Lihat laporan barang yang pernah kamu buat."), BorderLayout.NORTH);

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

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        headerRight.setOpaque(false);
        JButton btnBuat = UiKit.button("+ Buat Laporan", UiKit.BLUE, Color.WHITE);
        btnBuat.setPreferredSize(new Dimension(155, 42));
        btnBuat.addActionListener(e -> {
            dispose();
            new LaporHilangFrame().setVisible(true);
        });
        headerRight.add(btnBuat);

        historyHeader.add(headerLeft, BorderLayout.WEST);
        historyHeader.add(headerRight, BorderLayout.EAST);
        container.add(historyHeader, BorderLayout.NORTH);

        riwayatListPanel = new JPanel();
        riwayatListPanel.setOpaque(false);
        riwayatListPanel.setLayout(new BoxLayout(riwayatListPanel, BoxLayout.Y_AXIS));
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

    private void loadHistory() {
        riwayatListPanel.removeAll();
        try {
            List<Item> all = itemService.findAll();
            Integer myId = Session.getUser() != null ? Session.getUser().getId() : null;
            List<Item> mine = myId == null
                    ? all
                    : all.stream().filter(i -> myId.equals(i.getReportedByUserId())).toList();

            jumlahLaporanLabel.setText(mine.size() + " laporan ditemukan");

            if (mine.isEmpty()) {
                JPanel emptyPanel = new JPanel();
                emptyPanel.setBackground(new Color(248, 250, 252));
                emptyPanel.setBorder(new EmptyBorder(50, 20, 50, 20));
                emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));

                JLabel emptyTitle = UiKit.label("Belum Ada Laporan", 18, true, UiKit.TEXT);
                emptyTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
                JLabel emptySub = UiKit.label("Kamu belum membuat laporan barang.", 13, false, UiKit.MUTED);
                emptySub.setAlignmentX(Component.CENTER_ALIGNMENT);

                JButton btnEmpty = UiKit.button("Buat Laporan", UiKit.BLUE, Color.WHITE);
                btnEmpty.setPreferredSize(new Dimension(145, 40));
                btnEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);
                btnEmpty.addActionListener(e -> {
                    dispose();
                    new LaporHilangFrame().setVisible(true);
                });

                emptyPanel.add(emptyTitle);
                emptyPanel.add(Box.createVerticalStrut(8));
                emptyPanel.add(emptySub);
                emptyPanel.add(Box.createVerticalStrut(20));
                emptyPanel.add(btnEmpty);
                riwayatListPanel.add(emptyPanel);
            } else {
                for (int i = 0; i < mine.size(); i++) {
                    riwayatListPanel.add(createHistoryCard(mine.get(i)));
                    if (i < mine.size() - 1) {
                        riwayatListPanel.add(Box.createVerticalStrut(15));
                    }
                }
            }
        } catch (ServiceException ex) {
            UiKit.error(this, ex.getMessage());
        }
        riwayatListPanel.revalidate();
        riwayatListPanel.repaint();
    }

    private JPanel createHistoryCard(Item item) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(237, 240, 242), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel top = new JPanel(new BorderLayout(15, 0));
        top.setOpaque(false);

        boolean isLost = item.getType() == ItemType.LOST;
        String jenisLaporan = isLost ? "Barang Hilang" : "Barang Ditemukan";
        String statusText = item.getStatus() != null ? item.getStatus().label() : (isLost ? "Hilang" : "Ditemukan");

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(UiKit.label(item.getTitle(), 17, true, UiKit.TEXT));
        titleBox.add(Box.createVerticalStrut(5));
        titleBox.add(UiKit.label(jenisLaporan, 12, false, UiKit.MUTED));
        top.add(titleBox, BorderLayout.WEST);

        JLabel badge = new JLabel(statusText);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(6, 11, 6, 11));
        if (isLost) {
            badge.setBackground(new Color(253, 236, 234));
            badge.setForeground(new Color(231, 76, 60));
        } else {
            badge.setBackground(new Color(234, 248, 239));
            badge.setForeground(new Color(39, 174, 96));
        }

        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgeWrap.setOpaque(false);
        badgeWrap.add(badge);
        top.add(badgeWrap, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        JPanel detailGrid = new JPanel(new GridLayout(1, 3, 12, 0));
        detailGrid.setOpaque(false);
        detailGrid.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(240, 242, 243)),
                new EmptyBorder(15, 0, 0, 0)
        ));

        String katVal = item.getCategoryName() != null ? item.getCategoryName() : "Umum";
        String locVal = item.getLocation() != null ? item.getLocation() : "-";
        String tglVal = item.getIncidentAt() != null ? item.getIncidentAt().toLocalDate().toString() : "-";

        detailGrid.add(createDetailItem("Kategori", katVal));
        detailGrid.add(createDetailItem("Lokasi", locVal));
        detailGrid.add(createDetailItem("Tanggal", tglVal));

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
}