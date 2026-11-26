package com.foundit.view.user;

import com.foundit.controller.UserController;
import com.foundit.model.Account;
import com.foundit.model.Item;
import com.foundit.service.ServiceException;
import com.foundit.util.ImageHelper;
import com.foundit.util.Session;
import com.foundit.view.ThemeConfig;
import com.foundit.view.components.RoundedButton;
import com.foundit.view.components.RoundedPanel;
import com.foundit.view.components.StatusBadge;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

/**
 * UserDashboardPanel — Dashboard utama User identik dengan user/dashboard.php:
 * 4 kartu statistik, bagian Aksi Cepat, dan daftar 5 Laporan Terbaru.
 */
public class UserDashboardPanel extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final UserController controller;
    private final Consumer<String> navigator;

    private JLabel valHilang;
    private JLabel valDitemukan;
    private JLabel valKlaim;
    private JLabel valDikembalikan;
    private JPanel recentListPanel;

    public UserDashboardPanel(UserController controller, Consumer<String> navigator) {
        this.controller = controller;
        this.navigator = navigator;

        setOpaque(false);
        setLayout(new BorderLayout());
        add(buildScrollContent(), BorderLayout.CENTER);
    }

    private JScrollPane buildScrollContent() {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(0, 0, 16, 8));

        // 1. STATISTIK (4 KARTU)
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 102));
        statsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        valHilang = createStatValueLabel();
        valDitemukan = createStatValueLabel();
        valKlaim = createStatValueLabel();
        valDikembalikan = createStatValueLabel();

        statsGrid.add(buildStatCard("Barang Hilang", valHilang, ThemeConfig.BADGE_LOST_BG, ThemeConfig.BADGE_LOST_FG, "🔍"));
        statsGrid.add(buildStatCard("Barang Ditemukan", valDitemukan, ThemeConfig.BADGE_FOUND_BG, ThemeConfig.BADGE_FOUND_FG, "📦"));
        statsGrid.add(buildStatCard("Klaim Saya", valKlaim, ThemeConfig.BG_STAT_AMBER, ThemeConfig.FG_STAT_AMBER, "📝"));
        statsGrid.add(buildStatCard("Dikembalikan", valDikembalikan, ThemeConfig.BG_STAT_BLUE, ThemeConfig.FG_STAT_BLUE, "✅"));

        body.add(statsGrid);
        body.add(Box.createVerticalStrut(22));

        // 2. AKSI CEPAT
        RoundedPanel aksiCard = new RoundedPanel(ThemeConfig.CARD_RADIUS, ThemeConfig.BORDER_CARD, 1, false);
        aksiCard.setLayout(new BoxLayout(aksiCard, BoxLayout.Y_AXIS));
        aksiCard.setBorder(new EmptyBorder(20, 22, 20, 22));
        aksiCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 125));
        aksiCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel aksiTitle = new JLabel("Aksi Cepat");
        aksiTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        aksiTitle.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);
        aksiTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        aksiCard.add(aksiTitle);
        aksiCard.add(Box.createVerticalStrut(14));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton btnLaporHilang = RoundedButton.createDangerButton("Lapor Barang Hilang");
        btnLaporHilang.setPreferredSize(new Dimension(175, 40));
        btnLaporHilang.addActionListener(e -> navigator.accept("lapor-hilang"));

        RoundedButton btnLaporTemuan = new RoundedButton("Lapor Barang Ditemukan") {
            {
                setBackground(new Color(0x16A34A));
            }
        };
        btnLaporTemuan = createGreenButton("Lapor Barang Ditemukan");
        btnLaporTemuan.setPreferredSize(new Dimension(195, 40));
        btnLaporTemuan.addActionListener(e -> navigator.accept("lapor-ditemukan"));

        RoundedButton btnCari = RoundedButton.createOutlineButton("Cari Barang");
        btnCari.setPreferredSize(new Dimension(135, 40));
        btnCari.addActionListener(e -> navigator.accept("cari-barang"));

        btnRow.add(btnLaporHilang);
        btnRow.add(btnLaporTemuan);
        btnRow.add(btnCari);
        aksiCard.add(btnRow);

        body.add(aksiCard);
        body.add(Box.createVerticalStrut(24));

        // 3. HEADER LAPORAN TERBARU
        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setOpaque(false);
        recentHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        recentHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel rhLeft = new JPanel();
        rhLeft.setOpaque(false);
        rhLeft.setLayout(new BoxLayout(rhLeft, BoxLayout.Y_AXIS));
        JLabel rhTitle = new JLabel("Laporan Terbaru");
        rhTitle.setFont(ThemeConfig.FONT_TITLE);
        rhTitle.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);
        JLabel rhSub = new JLabel("Aktivitas laporan barang hilang dan ditemukan yang telah diterbitkan.");
        rhSub.setFont(ThemeConfig.FONT_REGULAR);
        rhSub.setForeground(ThemeConfig.COLOR_TEXT_MUTED);
        rhLeft.add(rhTitle);
        rhLeft.add(Box.createVerticalStrut(2));
        rhLeft.add(rhSub);

        JLabel viewAll = new JLabel("Lihat Semua →");
        viewAll.setFont(ThemeConfig.FONT_SEMIBOLD);
        viewAll.setForeground(ThemeConfig.COLOR_BRAND);
        viewAll.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewAll.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { navigator.accept("cari-barang"); }
        });

        recentHeader.add(rhLeft, BorderLayout.WEST);
        recentHeader.add(viewAll, BorderLayout.EAST);
        body.add(recentHeader);
        body.add(Box.createVerticalStrut(12));

        // 4. LIST LAPORAN TERBARU
        RoundedPanel listContainer = new RoundedPanel(ThemeConfig.CARD_RADIUS, ThemeConfig.BORDER_CARD, 1, false);
        listContainer.setLayout(new BorderLayout());
        listContainer.setBorder(new EmptyBorder(12, 18, 12, 18));
        listContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        recentListPanel = new JPanel();
        recentListPanel.setOpaque(false);
        recentListPanel.setLayout(new BoxLayout(recentListPanel, BoxLayout.Y_AXIS));
        listContainer.add(recentListPanel, BorderLayout.CENTER);

        body.add(listContainer);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private RoundedButton createGreenButton(String text) {
        return new RoundedButton(text, RoundedButton.Variant.PRIMARY, ThemeConfig.BTN_RADIUS_NORMAL) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0x15803D) : new Color(0x16A34A));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                        ThemeConfig.BTN_RADIUS_NORMAL, ThemeConfig.BTN_RADIUS_NORMAL);
                g2.dispose();
                setForeground(Color.WHITE);
                super.paintComponent(g);
            }
        };
    }

    private JLabel createStatValueLabel() {
        JLabel lbl = new JLabel("0");
        lbl.setFont(ThemeConfig.FONT_STAT_NUM);
        lbl.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);
        return lbl;
    }

    private RoundedPanel buildStatCard(String title, JLabel valLabel, Color iconBg, Color iconFg, String emoji) {
        RoundedPanel card = new RoundedPanel(ThemeConfig.CARD_RADIUS, ThemeConfig.BORDER_CARD, 1, false);
        card.setLayout(new BorderLayout(14, 0));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel iconBox = new JLabel(emoji, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(iconBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconBox.setPreferredSize(new Dimension(46, 46));
        iconBox.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconBox.setForeground(iconFg);

        JPanel meta = new JPanel();
        meta.setOpaque(false);
        meta.setLayout(new BoxLayout(meta, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(ThemeConfig.FONT_SMALL);
        lblTitle.setForeground(ThemeConfig.COLOR_TEXT_MUTED);

        meta.add(lblTitle);
        meta.add(Box.createVerticalStrut(4));
        meta.add(valLabel);

        card.add(iconBox, BorderLayout.WEST);
        card.add(meta, BorderLayout.CENTER);
        return card;
    }

    public void refreshData() {
        Account acc = Session.getCurrent();
        String userId = acc != null ? acc.getId() : null;

        try {
            int[] stats = controller.getDashboardStats(userId);
            valHilang.setText(String.valueOf(stats[0]));
            valDitemukan.setText(String.valueOf(stats[1]));
            valKlaim.setText(String.valueOf(stats[2]));
            valDikembalikan.setText(String.valueOf(stats[3]));

            List<Item> latest = controller.getLatestPublishedItems(5);
            renderRecentItems(latest);
        } catch (ServiceException ex) {
            recentListPanel.removeAll();
            JLabel err = new JLabel("Gagal memuat data dashboard: " + ex.getMessage());
            err.setForeground(ThemeConfig.STATUS_DITOLAK_FG);
            err.setFont(ThemeConfig.FONT_REGULAR);
            recentListPanel.add(err);
            recentListPanel.revalidate();
            recentListPanel.repaint();
        }
    }

    private void renderRecentItems(List<Item> items) {
        recentListPanel.removeAll();

        if (items.isEmpty()) {
            JLabel empty = new JLabel("Belum ada laporan barang yang diterbitkan.", SwingConstants.CENTER);
            empty.setFont(ThemeConfig.FONT_MEDIUM);
            empty.setForeground(ThemeConfig.COLOR_TEXT_MUTED);
            empty.setBorder(new EmptyBorder(24, 12, 24, 12));
            recentListPanel.add(empty);
        } else {
            for (int i = 0; i < items.size(); i++) {
                Item it = items.get(i);
                recentListPanel.add(buildRecentRow(it, i < items.size() - 1));
            }
        }

        recentListPanel.revalidate();
        recentListPanel.repaint();
    }

    private JPanel buildRecentRow(Item it, boolean withDivider) {
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                withDivider
                        ? BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeConfig.TABLE_ROW_BORDER)
                        : BorderFactory.createEmptyBorder(),
                new EmptyBorder(12, 8, 12, 8)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 102));

        // Foto 72x72
        JLabel photo = new JLabel(ImageHelper.loadScaledIcon(it.getPhoto(), 72, 72));
        photo.setPreferredSize(new Dimension(72, 72));
        row.add(photo, BorderLayout.WEST);

        // Info tengah
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(it.getTitle());
        title.setFont(ThemeConfig.FONT_BOLD);
        title.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);

        String catName = it.getCategoryName() != null ? it.getCategoryName() : "Tanpa kategori";
        JLabel meta = new JLabel(catName + "  •  " + it.getLocation());
        meta.setFont(ThemeConfig.FONT_SMALL);
        meta.setForeground(ThemeConfig.COLOR_TEXT_MUTED);

        JPanel badges = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        badges.setOpaque(false);
        badges.setAlignmentX(Component.LEFT_ALIGNMENT);
        badges.add(StatusBadge.forType(it.getType().getDb()));
        badges.add(StatusBadge.forItemStatus(it.getStatus().getDb()));

        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        meta.setAlignmentX(Component.LEFT_ALIGNMENT);

        center.add(title);
        center.add(Box.createVerticalStrut(3));
        center.add(meta);
        center.add(Box.createVerticalStrut(6));
        center.add(badges);
        row.add(center, BorderLayout.CENTER);

        // Tanggal kanan
        String dateText = it.getEventDate() != null ? it.getEventDate().format(DATE_FMT) : "-";
        JLabel dateLbl = new JLabel(dateText);
        dateLbl.setFont(ThemeConfig.FONT_SMALL);
        dateLbl.setForeground(ThemeConfig.COLOR_TEXT_MUTED);
        row.add(dateLbl, BorderLayout.EAST);

        return row;
    }
}
