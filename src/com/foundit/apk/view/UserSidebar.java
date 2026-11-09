package com.foundit.apk.view;

import java.awt.*;
import java.util.function.Supplier;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Sidebar navigasi kiri untuk halaman pengguna (dipakai ProfilFrame dan EditProfilFrame).
 * Gaya dan menu sama dengan sidebar di UserDashboardFrame.
 */
final class UserSidebar {

    private UserSidebar() { }

    static JPanel create(JFrame frame, String email, String role, String activeMenu) {
        JPanel side = new JPanel();
        side.setBackground(Color.WHITE);
        side.setPreferredSize(new Dimension(238, 0));
        side.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UiKit.BORDER));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));

        JPanel logo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logo.setOpaque(false);
        logo.setBorder(new EmptyBorder(27, 25, 32, 0));
        logo.add(UiKit.label("Found", 27, true, UiKit.BLUE_DARK));
        logo.add(UiKit.label("It", 27, true, UiKit.BLUE));
        side.add(logo);

        side.add(item(frame, "Dashboard", activeMenu, UserDashboardFrame.class,
                () -> new UserDashboardFrame(email, role)));
        side.add(item(frame, "Cari Barang", activeMenu, CariBarangFrame.class,
                () -> new CariBarangFrame(email, role)));
        side.add(item(frame, "Lapor Barang Hilang", activeMenu, LaporHilangFrame.class,
                () -> new LaporHilangFrame(email, role)));
        side.add(item(frame, "Lapor Barang Ditemukan", activeMenu, LaporDitemukanFrame.class,
                () -> new LaporDitemukanFrame(email, role)));
        side.add(item(frame, "Riwayat Laporan", activeMenu, RiwayatLaporanFrame.class,
                () -> new RiwayatLaporanFrame(email, role)));
        side.add(item(frame, "Status Klaim", activeMenu, StatusKlaimFrame.class,
                () -> new StatusKlaimFrame(email, role)));
        side.add(item(frame, "Profil", activeMenu, ProfilFrame.class,
                () -> new ProfilFrame(email, role)));

        side.add(Box.createVerticalGlue());

        side.add(item(frame, "Keluar", activeMenu, IndexFrame.class,
                () -> new IndexFrame()));
        side.add(Box.createVerticalStrut(25));

        return side;
    }

    private static JPanel item(JFrame frame, String text, String activeMenu,
                               Class<? extends JFrame> target, Supplier<JFrame> next) {
        boolean active = text.equals(activeMenu);

        JPanel p = new JPanel(new BorderLayout());
        p.setMaximumSize(new Dimension(220, 46));
        p.setBorder(new EmptyBorder(12, 18, 12, 10));
        p.setBackground(active ? UiKit.PALE : Color.WHITE);

        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(active ? UiKit.BLUE : UiKit.MUTED);
        p.add(label);

        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Sudah berada di halaman tersebut: tidak perlu membuka ulang.
                if (frame.getClass() == target) return;
                next.get().setVisible(true);
                frame.dispose();
            }
        });
        return p;
    }
}
