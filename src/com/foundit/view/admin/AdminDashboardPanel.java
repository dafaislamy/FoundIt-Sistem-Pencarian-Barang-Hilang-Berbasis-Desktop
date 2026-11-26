package com.foundit.view.admin;

import com.foundit.controller.AdminController;
import com.foundit.model.Item;
import com.foundit.service.ServiceException;
import com.foundit.util.Session;
import com.foundit.view.ThemeConfig;
import com.foundit.view.components.RoundedButton;
import com.foundit.view.components.RoundedPanel;
import com.foundit.view.components.StatusBadge;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

/**
 * AdminDashboardPanel — Dashboard utama Admin identik dengan admin/admin-dashboard.php:
 * Welcome banner, 4 kartu statistik, Aksi Pintas Admin (3 kartu), dan tabel Laporan Butuh Tindakan.
 */
public class AdminDashboardPanel extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final AdminController controller;
    private final Consumer<String> navigator;

    private JLabel valUsers;
    private JLabel valItems;
    private JLabel valPending;
    private JLabel valApprovedClaims;
    private JLabel pendingSubtitleLabel;

    private DefaultTableModel pendingTableModel;

    public AdminDashboardPanel(AdminController controller, Consumer<String> navigator) {
        this.controller = controller;
        this.navigator = navigator;

        setOpaque(false);
        setLayout(new BorderLayout());
        add(buildScroll(), BorderLayout.CENTER);
    }

    private JScrollPane buildScroll() {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(0, 0, 16, 8));

        // 1. WELCOME BANNER
        RoundedPanel welcome = RoundedPanel.createWelcomeCard();
        welcome.setLayout(new BorderLayout(20, 0));
        welcome.setBorder(new EmptyBorder(24, 28, 24, 28));
        welcome.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel wt = new JPanel();
        wt.setOpaque(false);
        wt.setLayout(new BoxLayout(wt, BoxLayout.Y_AXIS));

        JLabel halo = new JLabel("Halo, " + Session.getDisplayName());
        halo.setFont(ThemeConfig.FONT_SMALL);
        halo.setForeground(new Color(0xEAF5FC));

        JLabel wTitle = new JLabel("Pusat Kontrol & Verifikasi Barang Kampus");
        wTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        wTitle.setForeground(Color.WHITE);

        JLabel wSub = new JLabel("Pantau laporan barang, verifikasi bukti klaim mahasiswa, dan kelola ekosistem FoundIt secara terpusat.");
        wSub.setFont(ThemeConfig.FONT_REGULAR);
        wSub.setForeground(ThemeConfig.WELCOME_TEXT_SUB);

        wt.add(halo);
        wt.add(Box.createVerticalStrut(4));
        wt.add(wTitle);
        wt.add(Box.createVerticalStrut(6));
        wt.add(wSub);
        welcome.add(wt, BorderLayout.CENTER);

        RoundedButton verifyBtn = RoundedButton.createOutlineButton("Verifikasi Sekarang →");
        verifyBtn.setPreferredSize(new Dimension(185, 42));
        verifyBtn.addActionListener(e -> navigator.accept("admin-verifikasi-laporan"));
        JPanel btnWrap = new JPanel(new GridBagLayout());
        btnWrap.setOpaque(false);
        btnWrap.add(verifyBtn);
        welcome.add(btnWrap, BorderLayout.EAST);

        body.add(welcome);
        body.add(Box.createVerticalStrut(20));

        // 2. STATS CARDS (4 kolom)
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        statsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        valUsers = createStatValue(ThemeConfig.COLOR_TEXT_PRIMARY);
        valItems = createStatValue(ThemeConfig.COLOR_TEXT_PRIMARY);
        valPending = createStatValue(ThemeConfig.FG_STAT_AMBER);
        valApprovedClaims = createStatValue(ThemeConfig.COLOR_TEXT_PRIMARY);

        statsGrid.add(buildStatCard("Total Pengguna", valUsers, ThemeConfig.BG_STAT_BLUE, ThemeConfig.FG_STAT_BLUE, "👥"));
        statsGrid.add(buildStatCard("Laporan Barang", valItems, ThemeConfig.BG_STAT_PURPLE, ThemeConfig.FG_STAT_PURPLE, "📦"));
        statsGrid.add(buildStatCard("Perlu Verifikasi", valPending, ThemeConfig.BG_STAT_AMBER, ThemeConfig.FG_STAT_AMBER, "⏳"));
        statsGrid.add(buildStatCard("Klaim Selesai", valApprovedClaims, ThemeConfig.BG_STAT_EMERALD, ThemeConfig.FG_STAT_EMERALD, "🛡️"));

        body.add(statsGrid);
        body.add(Box.createVerticalStrut(22));

        // 3. AKSI PINTAS ADMIN
        JLabel aksiTitle = new JLabel("Aksi Pintas Admin");
        aksiTitle.setFont(ThemeConfig.FONT_TITLE);
        aksiTitle.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);
        aksiTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(aksiTitle);
        body.add(Box.createVerticalStrut(10));

        JPanel quickGrid = new JPanel(new GridLayout(1, 3, 16, 0));
        quickGrid.setOpaque(false);
        quickGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));
        quickGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        pendingSubtitleLabel = new JLabel("0 laporan menunggu peninjauan Anda.");
        pendingSubtitleLabel.setFont(ThemeConfig.FONT_SMALL);
        pendingSubtitleLabel.setForeground(ThemeConfig.COLOR_TEXT_MUTED);

        quickGrid.add(buildQuickCard("Verifikasi Laporan", pendingSubtitleLabel,
                ThemeConfig.BG_STAT_BLUE, "✓", "admin-verifikasi-laporan"));
        quickGrid.add(buildQuickCard("Verifikasi Klaim",
                createDescLabel("Cocokkan bukti kepemilikan barang dari pemohon."),
                ThemeConfig.BG_STAT_EMERALD, "🛡️", "admin-verifikasi-klaim"));
        quickGrid.add(buildQuickCard("Kelola Pengguna",
                createDescLabel("Lihat dan kelola akun mahasiswa terdaftar."),
                ThemeConfig.BG_STAT_PURPLE, "👥", "admin-kelola-user"));

        body.add(quickGrid);
        body.add(Box.createVerticalStrut(24));

        // 4. LAPORAN BUTUH TINDAKAN
        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        tableHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel thLeft = new JPanel();
        thLeft.setOpaque(false);
        thLeft.setLayout(new BoxLayout(thLeft, BoxLayout.Y_AXIS));
        JLabel thTitle = new JLabel("Laporan Butuh Tindakan");
        thTitle.setFont(ThemeConfig.FONT_TITLE);
        thTitle.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);
        JLabel thSub = new JLabel("Laporan barang hilang & ditemukan terbaru yang berstatus Pending.");
        thSub.setFont(ThemeConfig.FONT_SMALL);
        thSub.setForeground(ThemeConfig.COLOR_TEXT_MUTED);
        thLeft.add(thTitle);
        thLeft.add(thSub);

        JLabel viewAll = new JLabel("Lihat Semua →");
        viewAll.setFont(ThemeConfig.FONT_SEMIBOLD);
        viewAll.setForeground(ThemeConfig.COLOR_BRAND);
        viewAll.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewAll.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { navigator.accept("admin-verifikasi-laporan"); }
        });

        tableHeader.add(thLeft, BorderLayout.WEST);
        tableHeader.add(viewAll, BorderLayout.EAST);
        body.add(tableHeader);
        body.add(Box.createVerticalStrut(10));

        RoundedPanel tableCard = new RoundedPanel(ThemeConfig.CARD_RADIUS, ThemeConfig.BORDER_CARD, 1, false);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(14, 16, 14, 16));
        tableCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] cols = {"ID", "Barang", "Tipe", "Pelapor / Penemu", "Tanggal", "Status"};
        pendingTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(pendingTableModel);
        table.setRowHeight(44);
        table.setFont(ThemeConfig.FONT_MEDIUM);
        table.setShowVerticalLines(false);
        table.setGridColor(ThemeConfig.TABLE_ROW_BORDER);
        table.getTableHeader().setFont(ThemeConfig.FONT_SEMIBOLD);
        table.getTableHeader().setBackground(ThemeConfig.TABLE_HEADER_BG);
        table.getTableHeader().setForeground(ThemeConfig.TABLE_HEADER_FG);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));

        table.getColumnModel().getColumn(2).setCellRenderer(new TypeBadgeRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new PendingBadgeRenderer());

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(null);
        tableScroll.getViewport().setBackground(Color.WHITE);
        tableScroll.setPreferredSize(new Dimension(0, 260));
        tableCard.add(tableScroll, BorderLayout.CENTER);

        body.add(tableCard);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    public void refreshData() {
        try {
            int[] stats = controller.getDashboardStats();
            valUsers.setText(String.valueOf(stats[0]));
            valItems.setText(String.valueOf(stats[1]));
            valPending.setText(String.valueOf(stats[2]));
            valApprovedClaims.setText(String.valueOf(stats[3]));
            pendingSubtitleLabel.setText(stats[2] + " laporan menunggu peninjauan Anda.");

            pendingTableModel.setRowCount(0);
            List<Item> pending = controller.getPendingItems(5);
            for (Item it : pending) {
                String person = it.getReporterName() != null
                        ? it.getReporterName()
                        : (it.getFinderName() != null ? it.getFinderName() : "Anonim");
                String tgl = it.getEventDate() != null ? it.getEventDate().format(DATE_FMT) : "-";
                pendingTableModel.addRow(new Object[]{
                        it.getId(),
                        it.getTitle(),
                        it.getType().getDb(),
                        person,
                        tgl,
                        it.getStatus().getDb()
                });
            }
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat dashboard admin: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createStatValue(Color color) {
        JLabel lbl = new JLabel("0");
        lbl.setFont(ThemeConfig.FONT_STAT_NUM);
        lbl.setForeground(color);
        return lbl;
    }

    private JLabel createDescLabel(String text) {
        JLabel l = new JLabel("<html>" + text + "</html>");
        l.setFont(ThemeConfig.FONT_SMALL);
        l.setForeground(ThemeConfig.COLOR_TEXT_MUTED);
        return l;
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
        JLabel lbl = new JLabel(title);
        lbl.setFont(ThemeConfig.FONT_SMALL);
        lbl.setForeground(ThemeConfig.COLOR_TEXT_MUTED);
        meta.add(lbl);
        meta.add(Box.createVerticalStrut(4));
        meta.add(valLabel);

        card.add(iconBox, BorderLayout.WEST);
        card.add(meta, BorderLayout.CENTER);
        return card;
    }

    private RoundedPanel buildQuickCard(String title, JLabel descLabel, Color iconBg, String emoji, String route) {
        RoundedPanel card = new RoundedPanel(ThemeConfig.CARD_RADIUS, ThemeConfig.BORDER_CARD, 1, false);
        card.setLayout(new BorderLayout(14, 0));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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
        iconBox.setPreferredSize(new Dimension(44, 44));
        iconBox.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(title);
        t.setFont(ThemeConfig.FONT_BOLD);
        t.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);
        text.add(t);
        text.add(Box.createVerticalStrut(4));
        text.add(descLabel);

        card.add(iconBox, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { card.setBorderColor(ThemeConfig.COLOR_BRAND); }
            @Override public void mouseExited(MouseEvent e) { card.setBorderColor(ThemeConfig.BORDER_CARD); }
            @Override public void mouseClicked(MouseEvent e) { navigator.accept(route); }
        });
        return card;
    }

    private static class TypeBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 9));
            p.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            p.add(StatusBadge.forType(value != null ? value.toString() : "lost"));
            return p;
        }
    }

    private static class PendingBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 9));
            p.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            p.add(StatusBadge.forItemStatus(value != null ? value.toString() : "Pending"));
            return p;
        }
    }
}
