package com.foundit.view;

import com.foundit.model.Claim;
import com.foundit.model.ClaimStatus;
import com.foundit.model.Item;
import com.foundit.model.ItemStatus;
import com.foundit.service.ClaimService;
import com.foundit.service.ItemService;
import com.foundit.service.ServiceException;
import com.foundit.service.UserService;
import com.foundit.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardFrame extends JFrame {

    private enum IconType {
        USERS, ITEMS, PENDING, COMPLETED
    }

    private final UserService userService = new UserService();
    private final ItemService itemService = new ItemService();
    private final ClaimService claimService = new ClaimService();

    private JLabel valUsers, valItems, valPending, valCompleted;
    private DefaultTableModel tableModel;
    private JTable table;
    private final List<String[]> rowDescriptions = new ArrayList<>();

    public AdminDashboardFrame() {
        setTitle("Dashboard Admin - FoundIt");
        setSize(1150, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(new AdminSidebarPanel(this, AdminSidebarPanel.Menu.DASHBOARD), BorderLayout.WEST);

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainContent.setBackground(new Color(247, 249, 252));

        JPanel topbar = AdminSidebarPanel.buildTopbar(
                "Dashboard Admin",
                "Selamat datang di panel kendali sistem FoundIt."
        );
        topbar.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(topbar);
        mainContent.add(Box.createRigidArea(new Dimension(0, 25)));

        JPanel welcomeCard = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setPaint(new GradientPaint(0, 0, new Color(52, 152, 219), getWidth(), getHeight(), new Color(44, 62, 80)));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        welcomeCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        welcomeCard.setBorder(new EmptyBorder(25, 30, 25, 30));
        welcomeCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 135));

        JPanel wcText = new JPanel();
        wcText.setLayout(new BoxLayout(wcText, BoxLayout.Y_AXIS));
        wcText.setOpaque(false);

        JLabel badgeLabel = new JLabel(" Halo " + Session.getDisplayName() + " ");
        badgeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        badgeLabel.setOpaque(true);
        badgeLabel.setBackground(new Color(255, 255, 255, 50));
        badgeLabel.setForeground(Color.WHITE);
        badgeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel wcTitle = new JLabel("Pusat Kontrol & Verifikasi Barang Kampus");
        wcTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        wcTitle.setForeground(Color.WHITE);
        wcTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel wcDesc = new JLabel("Kelola pengguna, pantau laporan yang masuk, dan selesaikan verifikasi kepemilikan barang dengan cepat.");
        wcDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        wcDesc.setForeground(new Color(234, 245, 252));
        wcDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        wcText.add(badgeLabel);
        wcText.add(Box.createRigidArea(new Dimension(0, 8)));
        wcText.add(wcTitle);
        wcText.add(Box.createRigidArea(new Dimension(0, 5)));
        wcText.add(wcDesc);
        welcomeCard.add(wcText, BorderLayout.WEST);

        mainContent.add(welcomeCard);
        mainContent.add(Box.createRigidArea(new Dimension(0, 25)));

        valUsers = new JLabel("0");
        valItems = new JLabel("0");
        valPending = new JLabel("0");
        valCompleted = new JLabel("0");

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));
        statsPanel.add(createStatCard("Total Pengguna", valUsers, IconType.USERS));
        statsPanel.add(createStatCard("Laporan Barang", valItems, IconType.ITEMS));
        statsPanel.add(createStatCard("Perlu Verifikasi", valPending, IconType.PENDING));
        statsPanel.add(createStatCard("Klaim Selesai", valCompleted, IconType.COMPLETED));

        mainContent.add(statsPanel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 25)));

        JPanel tableHeaderWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tableHeaderWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        tableHeaderWrap.setOpaque(false);
        tableHeaderWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel tableLabel = new JLabel("Laporan Menunggu Verifikasi");
        tableLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableLabel.setForeground(new Color(44, 62, 80));
        tableHeaderWrap.add(tableLabel);

        mainContent.add(tableHeaderWrap);
        mainContent.add(Box.createRigidArea(new Dimension(0, 12)));

        String[] columns = {"Barang", "Tipe", "Lokasi", "Status", "Aksi"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(10, 6));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));

        configureColumns();

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (table.columnAtPoint(e.getPoint()) == 4 && table.rowAtPoint(e.getPoint()) >= 0) {
                    dispose();
                    new AdminVerifikasiLaporanFrame().setVisible(true);
                }
            }
        });

        table.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustRowHeights();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setPreferredSize(new Dimension(0, 240));
        scrollPane.setMinimumSize(new Dimension(0, 180));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
        mainContent.add(scrollPane);

        JScrollPane mainScroll = new JScrollPane(mainContent);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);

        loadStatsAndTable();
    }

    private void configureColumns() {
        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setPreferredWidth(360);
        cm.getColumn(1).setPreferredWidth(100);
        cm.getColumn(2).setPreferredWidth(190);
        cm.getColumn(3).setPreferredWidth(100);
        cm.getColumn(4).setPreferredWidth(80);

        cm.getColumn(0).setCellRenderer((tbl, value, isSelected, hasFocus, row, column) -> {
            JPanel cell = new JPanel();
            cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
            cell.setBorder(new EmptyBorder(8, 8, 8, 8));
            cell.setBackground(isSelected ? tbl.getSelectionBackground() : Color.WHITE);

            String title = row < rowDescriptions.size() ? rowDescriptions.get(row)[0] : String.valueOf(value);
            String desc = row < rowDescriptions.size() ? rowDescriptions.get(row)[1] : "";

            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblTitle.setForeground(isSelected ? tbl.getSelectionForeground() : new Color(33, 37, 41));
            lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextArea taDesc = new JTextArea(desc);
            taDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            taDesc.setForeground(isSelected ? tbl.getSelectionForeground() : new Color(119, 119, 119));
            taDesc.setBackground(isSelected ? tbl.getSelectionBackground() : Color.WHITE);
            taDesc.setLineWrap(true);
            taDesc.setWrapStyleWord(true);
            taDesc.setEditable(false);
            taDesc.setFocusable(false);
            taDesc.setBorder(null);
            taDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

            cell.add(lblTitle);
            cell.add(Box.createRigidArea(new Dimension(0, 3)));
            cell.add(taDesc);
            return cell;
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                setVerticalAlignment(SwingConstants.CENTER);
                setHorizontalAlignment(column == 4 ? SwingConstants.CENTER : SwingConstants.LEFT);
                if (!isSelected) {
                    setBackground(Color.WHITE);
                }
                return c;
            }
        };

        for (int col = 1; col < cm.getColumnCount(); col++) {
            cm.getColumn(col).setCellRenderer(centerRenderer);
        }
    }

    private void adjustRowHeights() {
        int colWidth = table.getColumnModel().getColumn(0).getWidth();
        if (colWidth <= 0) {
            colWidth = 340;
        }
        int textWidth = Math.max(140, colWidth - 20);

        for (int row = 0; row < table.getRowCount(); row++) {
            String title = row < rowDescriptions.size() ? rowDescriptions.get(row)[0] : "";
            String desc = row < rowDescriptions.size() ? rowDescriptions.get(row)[1] : "";

            JLabel titleProbe = new JLabel(title);
            titleProbe.setFont(new Font("Segoe UI", Font.BOLD, 13));
            int titleHeight = titleProbe.getPreferredSize().height;

            JTextArea descProbe = new JTextArea(desc);
            descProbe.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            descProbe.setLineWrap(true);
            descProbe.setWrapStyleWord(true);
            descProbe.setSize(new Dimension(textWidth, Short.MAX_VALUE));
            int descHeight = descProbe.getPreferredSize().height;

            int neededHeight = Math.max(58, titleHeight + descHeight + 24);
            table.setRowHeight(row, neededHeight);
        }
    }

    private void loadStatsAndTable() {
        try {
            int totalUsers = userService.findAll().size();
            List<Item> items = itemService.findAll();
            List<Claim> claims = claimService.findAll();

            long pendingItems = items.stream().filter(i -> i.getStatus() == ItemStatus.PENDING).count();
            long pendingClaims = claims.stream().filter(c -> c.getStatus() == ClaimStatus.MENUNGGU).count();
            long approvedClaims = claims.stream().filter(c -> c.getStatus() == ClaimStatus.DISETUJUI).count();

            valUsers.setText(String.valueOf(totalUsers));
            valItems.setText(String.valueOf(items.size()));
            valPending.setText(String.valueOf(pendingItems));
            valPending.setToolTipText("Laporan barang menunggu verifikasi: " + pendingItems + " | Klaim menunggu: " + pendingClaims);
            valCompleted.setText(String.valueOf(approvedClaims));

            tableModel.setRowCount(0);
            rowDescriptions.clear();

            for (Item it : items) {
                if (it.getStatus() == ItemStatus.PENDING) {
                    String title = it.getTitle() != null ? it.getTitle() : "-";
                    String desc = it.getDescription() != null ? it.getDescription() : "";
                    rowDescriptions.add(new String[]{title, desc});

                    tableModel.addRow(new Object[]{
                            title,
                            it.getType() != null ? it.getType().toString() : "-",
                            it.getLocation() != null ? it.getLocation() : "-",
                            it.getStatus().toString(),
                            "<html><font color='#3498db'><u>Tinjau</u></font></html>"
                    });
                }
            }

            SwingUtilities.invokeLater(this::adjustRowHeights);
        } catch (ServiceException ex) {
            UiKit.error(this, ex.getMessage());
        }
    }

    private JPanel createStatCard(String title, JLabel valueLabel, IconType iconType) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                new EmptyBorder(15, 20, 15, 20)
        ));

        card.add(new StatIconPanel(iconType), BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setForeground(new Color(119, 119, 119));
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(new Color(44, 62, 80));

        info.add(t);
        info.add(valueLabel);
        card.add(info, BorderLayout.CENTER);
        return card;
    }

    private static class StatIconPanel extends JPanel {
        private final IconType type;

        StatIconPanel(IconType type) {
            this.type = type;
            setPreferredSize(new Dimension(45, 45));
            setMinimumSize(new Dimension(45, 45));
            setMaximumSize(new Dimension(45, 45));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(238, 246, 252));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

            g2.setColor(new Color(52, 152, 219));
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (type) {
                case USERS -> {
                    g2.drawOval(16, 11, 10, 10);
                    g2.drawArc(11, 22, 20, 14, 0, 180);
                    g2.drawOval(27, 13, 7, 7);
                    g2.drawArc(24, 22, 14, 12, 0, 180);
                }
                case ITEMS -> {
                    g2.drawRoundRect(12, 14, 21, 18, 3, 3);
                    g2.drawLine(12, 20, 33, 20);
                    g2.drawLine(20, 24, 25, 24);
                }
                case PENDING -> {
                    g2.drawLine(14, 12, 31, 12);
                    g2.drawLine(14, 33, 31, 33);
                    g2.drawLine(16, 12, 29, 33);
                    g2.drawLine(29, 12, 16, 33);
                }
                case COMPLETED -> {
                    g2.drawOval(11, 11, 23, 23);
                    g2.drawLine(17, 23, 21, 27);
                    g2.drawLine(21, 27, 29, 18);
                }
            }

            g2.dispose();
        }
    }
}