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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Dashboard Admin terhubung langsung ke UserService, ItemService, dan ClaimService (Database).
 */
public class AdminDashboardFrame extends JFrame {

    private final UserService userService = new UserService();
    private final ItemService itemService = new ItemService();
    private final ClaimService claimService = new ClaimService();

    private JLabel valUsers, valItems, valPending, valCompleted;
    private DefaultTableModel tableModel;

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

        mainContent.add(AdminSidebarPanel.buildTopbar(
                "Dashboard Admin",
                "Selamat datang di panel kendali sistem FoundIt."
        ));
        mainContent.add(Box.createRigidArea(new Dimension(0, 25)));

        JPanel welcomeCard = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, new Color(52, 152, 219), getWidth(), getHeight(), new Color(44, 62, 80)));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        welcomeCard.setBorder(new EmptyBorder(25, 30, 25, 30));
        welcomeCard.setMaximumSize(new Dimension(2000, 130));

        JPanel wcText = new JPanel();
        wcText.setLayout(new BoxLayout(wcText, BoxLayout.Y_AXIS));
        wcText.setOpaque(false);

        JLabel badgeLabel = new JLabel(" Halo " + Session.getDisplayName() + " ");
        badgeLabel.setOpaque(true);
        badgeLabel.setBackground(new Color(255, 255, 255, 50));
        badgeLabel.setForeground(Color.WHITE);
        badgeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel wcTitle = new JLabel("Pusat Kontrol & Verifikasi Barang Kampus");
        wcTitle.setForeground(Color.WHITE);
        wcTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel wcDesc = new JLabel("Kelola pengguna, pantau laporan yang masuk, dan selesaikan verifikasi kepemilikan barang dengan cepat.");
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
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(2000, 95));
        statsPanel.add(createStatCard("Total Pengguna", valUsers, "👥"));
        statsPanel.add(createStatCard("Laporan Barang", valItems, "📦"));
        statsPanel.add(createStatCard("Perlu Verifikasi", valPending, "⏳"));
        statsPanel.add(createStatCard("Klaim Selesai", valCompleted, "✓"));

        mainContent.add(statsPanel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 25)));

        JLabel tableLabel = new JLabel("Laporan Menunggu Verifikasi");
        tableLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        mainContent.add(tableLabel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 12)));

        String[] columns = {"Barang", "Tipe", "Lokasi", "Status", "Aksi"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(55);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (table.columnAtPoint(e.getPoint()) == 4 && table.rowAtPoint(e.getPoint()) >= 0) {
                    dispose();
                    new AdminVerifikasiLaporanFrame().setVisible(true);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setMaximumSize(new Dimension(2000, 190));
        mainContent.add(scrollPane);

        add(new JScrollPane(mainContent), BorderLayout.CENTER);
        loadStatsAndTable();
    }

    private void loadStatsAndTable() {
        try {
            int totalUsers = userService.findAll().size();
            List<Item> items = itemService.findAll();
            List<Claim> claims = claimService.findAll();

            long pendingItems = items.stream().filter(i -> i.getStatus() == ItemStatus.PENDING).count();
            long pendingClaims = claims.stream().filter(c -> c.getStatus() == ClaimStatus.PENDING).count();
            long approvedClaims = claims.stream().filter(c -> c.getStatus() == ClaimStatus.APPROVED).count();

            valUsers.setText(String.valueOf(totalUsers));
            valItems.setText(String.valueOf(items.size()));
            valPending.setText(String.valueOf(pendingItems + pendingClaims));
            valCompleted.setText(String.valueOf(approvedClaims));

            tableModel.setRowCount(0);
            for (Item it : items) {
                if (it.getStatus() == ItemStatus.PENDING) {
                    String desc = it.getDescription() != null ? it.getDescription() : "";
                    tableModel.addRow(new Object[]{
                            "<html><b>" + it.getTitle() + "</b><br><font color='#777'>" + desc + "</font></html>",
                            it.getType() != null ? it.getType().label() : "-",
                            it.getLocation() != null ? it.getLocation() : "-",
                            it.getStatus().label(),
                            "<html><font color='#3498db'><u>Tinjau</u></font></html>"
                    });
                }
            }
        } catch (ServiceException ex) {
            UiKit.error(this, ex.getMessage());
        }
    }

    private JPanel createStatCard(String title, JLabel valueLabel, String icon) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblIcon = new JLabel(icon, JLabel.CENTER);
        lblIcon.setOpaque(true);
        lblIcon.setBackground(new Color(238, 246, 252));
        lblIcon.setPreferredSize(new Dimension(45, 45));
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        card.add(lblIcon, BorderLayout.WEST);

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
}