package com.foundit.view;

import com.foundit.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminSidebarPanel extends JPanel {

    public enum Menu {
        DASHBOARD("Dashboard"),
        KELOLA_KATEGORI("Kelola Kategori"),
        KELOLA_LAPORAN("Kelola Laporan Barang"),
        KELOLA_USER("Kelola User"),
        UBAH_STATUS("Ubah Status Barang"),
        VERIFIKASI_KLAIM("Verifikasi Klaim"),
        VERIFIKASI_LAPORAN("Verifikasi Laporan");

        public final String label;

        Menu(String label) {
            this.label = label;
        }
    }

    public AdminSidebarPanel(JFrame owner, Menu activeMenu) {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(229, 231, 235)));
        setPreferredSize(new Dimension(240, owner.getHeight()));
        setLayout(new BorderLayout());

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 25));
        logoPanel.setOpaque(false);
        JLabel logoLabel = new JLabel("<html>Found<font color='#3498db'>It</font></html>");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoPanel.add(logoLabel);
        add(logoPanel, BorderLayout.NORTH);

        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        for (Menu m : Menu.values()) {
            boolean isActive = m == activeMenu;
            JButton btnMenu = new JButton(m.label);
            btnMenu.setHorizontalAlignment(SwingConstants.LEFT);
            btnMenu.setMaximumSize(new Dimension(210, 42));
            btnMenu.setFocusPainted(false);
            btnMenu.setBorderPainted(false);
            btnMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            if (isActive) {
                btnMenu.setBackground(new Color(234, 245, 252));
                btnMenu.setForeground(new Color(52, 152, 219));
                btnMenu.setFont(new Font("Segoe UI", Font.BOLD, 14));
            } else {
                btnMenu.setBackground(Color.WHITE);
                btnMenu.setForeground(new Color(102, 112, 133));
                btnMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            }

            btnMenu.addActionListener(e -> {
                if (isActive) return;
                owner.dispose();
                switch (m) {
                    case DASHBOARD -> new AdminDashboardFrame().setVisible(true);
                    case KELOLA_KATEGORI -> new AdminKelolaKategoriFrame().setVisible(true);
                    case KELOLA_LAPORAN -> new AdminKelolaLaporanFrame().setVisible(true);
                    case KELOLA_USER -> new AdminKelolaUserFrame().setVisible(true);
                    case UBAH_STATUS -> new AdminUbahStatusFrame().setVisible(true);
                    case VERIFIKASI_KLAIM -> new AdminVerifikasiKlaimFrame().setVisible(true);
                    case VERIFIKASI_LAPORAN -> new AdminVerifikasiLaporanFrame().setVisible(true);
                }
            });

            navPanel.add(btnMenu);
            navPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        }

        navPanel.add(Box.createVerticalGlue());

        JButton btnLogout = new JButton("Keluar");
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.setMaximumSize(new Dimension(210, 42));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setForeground(new Color(220, 53, 69));
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            Session.logout();
            owner.dispose();
            new IndexFrame().setVisible(true);
        });
        navPanel.add(btnLogout);

        add(navPanel, BorderLayout.CENTER);
    }

    public static JPanel buildTopbar(String heading, String subheading) {
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setOpaque(false);
        topbar.setMaximumSize(new Dimension(2000, 65));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        JLabel h1 = new JLabel(heading);
        h1.setFont(new Font("Segoe UI", Font.BOLD, 26));
        JLabel subH1 = new JLabel(subheading);
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

        JLabel avatar = new JLabel(" " + Session.getInitial() + " ", JLabel.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(new Color(52, 152, 219));
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        avatar.setPreferredSize(new Dimension(38, 38));

        String adminName = Session.getCurrent() != null ? Session.getDisplayName() : "Administrator";
        JLabel adminInfo = new JLabel("<html><b>" + adminName + "</b><br><font color='#888'>Admin</font></html>");
        profilePanel.add(avatar);
        profilePanel.add(adminInfo);
        topbar.add(profilePanel, BorderLayout.EAST);

        return topbar;
    }
}