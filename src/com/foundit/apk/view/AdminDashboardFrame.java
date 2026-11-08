package com.foundit.apk.view;

import com.foundit.apk.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class AdminDashboardFrame extends JFrame {
    public AdminDashboardFrame() {
        setTitle("Dashboard Admin - FoundIt");
        setSize(1150, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        User activeAdmin = new User("A1", "Administrator", "ADM001", "admin@campus.ac.id", "secret", "081111111111", Role.ADMIN);
        Item item1 = new Item("I1", "Kunci Motor Honda Vario", "Gantungan hitam bordir", ItemType.FIND, "Gedung C", LocalDate.now(), null, null);

        JPanel sidebar = new JPanel();
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(229, 231, 235)));
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setLayout(new BorderLayout());

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 25));
        logoPanel.setOpaque(false);
        JLabel logoLabel = new JLabel("<html>Found<font color='#3498db'>It</font></html>");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoPanel.add(logoLabel);
        sidebar.add(logoPanel, BorderLayout.NORTH);

        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        String[] menuItems = {
            "Dashboard", "Kelola Kategori", "Kelola Laporan Barang", 
            "Kelola User", "Ubah Status Barang", "Verifikasi Klaim", "Verifikasi Laporan"
        };

        for (int i = 0; i < menuItems.length; i++) {
            JButton btnMenu = new JButton(menuItems[i]);
            btnMenu.setHorizontalAlignment(SwingConstants.LEFT);
            btnMenu.setMaximumSize(new Dimension(210, 42));
            btnMenu.setFocusPainted(false);
            btnMenu.setBorderPainted(false);
            
            if (i == 0) {
                btnMenu.setBackground(new Color(234, 245, 252));
                btnMenu.setForeground(new Color(52, 152, 219));
                btnMenu.setFont(new Font("Segoe UI", Font.BOLD, 14));
            } else {
                btnMenu.setBackground(Color.WHITE);
                btnMenu.setForeground(new Color(102, 112, 133));
                btnMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            }
            
            final int index = i;
btnMenu.addActionListener(e -> {
                dispose(); // Menutup frame yang sedang aktif
                
                if (index == 0) {
                    new AdminDashboardFrame().setVisible(true);
                } else if (index == 1) {
                    new AdminKelolaKategoriFrame().setVisible(true);
                } else if (index == 2) {
                    new AdminKelolaLaporanFrame().setVisible(true);
                } else if (index == 3) {
                    new AdminKelolaUserFrame().setVisible(true);
                } else if (index == 4) {
                    new AdminUbahStatusFrame().setVisible(true);
                } else if (index == 5) {
                    new AdminVerifikasiKlaimFrame().setVisible(true);
                } else if (index == 6) {
                    new AdminVerifikasiLaporanFrame().setVisible(true);
                }
            });

            navPanel.add(btnMenu);
            navPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        }
        sidebar.add(navPanel, BorderLayout.CENTER);
        add(sidebar, BorderLayout.WEST);

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainContent.setBackground(new Color(247, 249, 252));

        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setOpaque(false);
        topbar.setMaximumSize(new Dimension(2000, 65));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        JLabel h1 = new JLabel("Dashboard Admin");
        h1.setFont(new Font("Segoe UI", Font.BOLD, 26));
        JLabel subH1 = new JLabel("Selamat datang di panel kendali sistem FoundIt.");
        subH1.setForeground(new Color(119, 119, 119));
        subH1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titlePanel.add(h1);
        titlePanel.add(subH1);
        topbar.add(titlePanel, BorderLayout.WEST);

        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
        profilePanel.setOpaque(false);
        profilePanel.setBackground(Color.WHITE);
        profilePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            new EmptyBorder(4, 12, 4, 12)
        ));
        
        JLabel avatar = new JLabel(" A ", JLabel.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(new Color(52, 152, 219));
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        avatar.setPreferredSize(new Dimension(38, 38));
        
        JLabel adminInfo = new JLabel("<html><b>" + activeAdmin.getNama() + "</b><br><font color='#888'>Super Admin</font></html>");
        profilePanel.add(avatar);
        profilePanel.add(adminInfo);
        topbar.add(profilePanel, BorderLayout.EAST);

        mainContent.add(topbar);
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
        JLabel badgeLabel = new JLabel(" Halo Admin 👋 ");
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

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(2000, 95));
        statsPanel.add(createStatCard("Total Pengguna", "248", "👥"));
        statsPanel.add(createStatCard("Laporan Barang", "54", "📦"));
        statsPanel.add(createStatCard("Perlu Verifikasi", "8", "⏳"));
        statsPanel.add(createStatCard("Klaim Selesai", "36", "✓"));
        
        mainContent.add(statsPanel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 25)));

        JLabel tableLabel = new JLabel("Laporan Butuh Tindakan");
        tableLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        mainContent.add(tableLabel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 12)));

        String[] columns = {"Barang", "Tipe", "Pelapor", "Tanggal", "Status", "Aksi"};
        Object[][] data = {
            {"<html><b>" + item1.getJudul() + "</b><br><font color='#777'>" + item1.getDeskripsi() + "</font></html>", "Ditemukan", "Budi Santoso (FT)", "24 Sep 2026", "Menunggu Verifikasi", "Tinjau"}
        };
        
        DefaultTableModel model = new DefaultTableModel(data, columns);
        JTable table = new JTable(model);
        table.setRowHeight(55);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setMaximumSize(new Dimension(2000, 160));
        mainContent.add(scrollPane);

        add(new JScrollPane(mainContent), BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, String value, String icon) {
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
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 22));
        v.setForeground(new Color(44, 62, 80));
        
        info.add(t);
        info.add(v);
        card.add(info, BorderLayout.CENTER);
        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboardFrame().setVisible(true));
    }
}