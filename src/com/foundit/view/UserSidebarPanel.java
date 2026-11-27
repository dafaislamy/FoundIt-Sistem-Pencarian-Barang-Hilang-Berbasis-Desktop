package com.foundit.view;

import com.foundit.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UserSidebarPanel extends JPanel {

    public enum Menu {
        DASHBOARD("Dashboard", "home"),
        CARI_BARANG("Cari Barang", "search"),
        LAPOR_HILANG("Lapor Barang Hilang", "lost"),
        LAPOR_DITEMUKAN("Lapor Barang Ditemukan", "found"),
        KLAIM_BARANG("Klaim Barang", "claim"),
        STATUS_KLAIM("Status Klaim", "status"),
        RIWAYAT_LAPORAN("Riwayat Laporan", "history"),
        PROFIL("Profil", "profile");

        public final String label;
        public final String iconKind;

        Menu(String label, String iconKind) {
            this.label = label;
            this.iconKind = iconKind;
        }
    }

    public UserSidebarPanel(JFrame owner, Menu activeMenu) {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(238, 0));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UiKit.BORDER));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel logo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logo.setOpaque(false);
        logo.setBorder(new EmptyBorder(24, 22, 18, 0));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        logo.add(UiKit.label("Found", 27, true, UiKit.BLUE_DARK));
        logo.add(UiKit.label("It", 27, true, UiKit.BLUE));
        add(logo);

        for (Menu m : Menu.values()) {
            add(createItem(owner, m, m == activeMenu));
        }

        add(Box.createVerticalGlue());

        JPanel logoutItem = createActionRow("Keluar", "return", false, () -> {
            Session.logout();
            owner.dispose();
            new IndexFrame().setVisible(true);
        });
        add(logoutItem);
        add(Box.createVerticalStrut(25));
    }

    private JPanel createItem(JFrame owner, Menu menu, boolean active) {
        return createActionRow(menu.label, menu.iconKind, active, () -> {
            if (active) return;
            owner.dispose();
            switch (menu) {
                case DASHBOARD -> new UserDashboardFrame().setVisible(true);
                case CARI_BARANG -> new CariBarangFrame().setVisible(true);
                case LAPOR_HILANG -> new LaporHilangFrame().setVisible(true);
                case LAPOR_DITEMUKAN -> new LaporDitemukanFrame().setVisible(true);
                case KLAIM_BARANG -> new KlaimFrame().setVisible(true);
                case STATUS_KLAIM -> new StatusKlaimFrame().setVisible(true);
                case RIWAYAT_LAPORAN -> new RiwayatLaporanFrame().setVisible(true);
                case PROFIL -> new ProfilFrame().setVisible(true);
            }
        });
    }

    private JPanel createActionRow(String text, String iconKind, boolean active, Runnable action) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(220, 44));
        p.setBorder(new EmptyBorder(10, 18, 10, 10));
        p.setBackground(active ? UiKit.PALE : Color.WHITE);

        JLabel iconLbl = new JLabel(UiKit.icon(iconKind, active ? UiKit.BLUE : UiKit.MUTED, 16));
        JLabel item = new JLabel(text);
        item.setHorizontalAlignment(SwingConstants.LEFT);
        item.setFont(new Font("Segoe UI", Font.BOLD, 13));
        item.setForeground(active ? UiKit.BLUE : UiKit.MUTED);

        p.add(iconLbl, BorderLayout.WEST);
        p.add(item, BorderLayout.CENTER);
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });
        return p;
    }

    public static JPanel buildTopbar(String title, String subtitle) {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UiKit.header(title, subtitle), BorderLayout.WEST);

        JPanel profile = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        profile.setOpaque(false);

        JLabel avatar = UiKit.label(Session.getInitial(), 16, true, Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(UiKit.BLUE);
        avatar.setPreferredSize(new Dimension(40, 40));
        profile.add(avatar);

        JPanel names = new JPanel();
        names.setOpaque(false);
        names.setLayout(new BoxLayout(names, BoxLayout.Y_AXIS));
        names.add(UiKit.label(Session.getDisplayName(), 13, true, UiKit.TEXT));
        names.add(UiKit.label(Session.getDisplayEmail(), 11, false, UiKit.MUTED));
        profile.add(names);

        top.add(profile, BorderLayout.EAST);
        return top;
    }
}