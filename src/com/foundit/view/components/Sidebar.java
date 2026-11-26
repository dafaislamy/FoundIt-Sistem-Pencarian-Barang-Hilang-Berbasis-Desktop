package com.foundit.view.components;

import com.foundit.view.ThemeConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Sidebar — panel navigasi lateral aplikasi FoundIt Desktop.
 *
 * Meniru .sidebar CSS web:
 *   - width: 240px; fixed left; bg: white; border-right: 1px solid #e5e7eb
 *   - .logo: font-size 28px bold; margin-bottom 40px; "Found"[biru]"It"
 *   - nav a: color #667085; padding 12px 14px; border-radius 8px
 *   - nav a:hover: bg #f0f7fc; color #3498db
 *   - nav a.active: bg #eaf5fc; color #3498db; font-weight 600
 *   - .logout: margin-top auto; color #e74c3c; hover bg #fdf0ef
 */
public class Sidebar extends JPanel {

    private final List<SidebarItem> items = new ArrayList<>();
    private SidebarItem activeItem = null;
    private Consumer<String> onNavigate;

    public Sidebar() {
        setPreferredSize(new Dimension(ThemeConfig.SIDEBAR_WIDTH, 0));
        setBackground(ThemeConfig.BG_SIDEBAR);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeConfig.BORDER_SIDEBAR));
        buildUI();
    }

    private void buildUI() {
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(
                ThemeConfig.SIDEBAR_PADDING + 10, ThemeConfig.SIDEBAR_PADDING,
                0, ThemeConfig.SIDEBAR_PADDING));

        // ===== LOGO =====
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoPanel.setOpaque(false);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));

        JLabel logoFound = new JLabel("Found");
        logoFound.setFont(ThemeConfig.FONT_LOGO);
        logoFound.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);

        JLabel logoIt = new JLabel("It");
        logoIt.setFont(ThemeConfig.FONT_LOGO);
        logoIt.setForeground(ThemeConfig.COLOR_BRAND);

        logoPanel.add(logoFound);
        logoPanel.add(logoIt);
        topPanel.add(logoPanel);

        // ===== NAV ITEMS container =====
        JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.putClientProperty("navPanel", true);
        topPanel.add(navPanel);

        add(topPanel, BorderLayout.CENTER);

        // ===== LOGOUT BUTTON =====
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(
                0, ThemeConfig.SIDEBAR_PADDING, ThemeConfig.SIDEBAR_PADDING + 10,
                ThemeConfig.SIDEBAR_PADDING));
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Tambah menu item ke sidebar.
     * @param label teks yang ditampilkan
     * @param key   kunci navigasi (dipakai di onNavigate callback)
     * @param isActive apakah item ini aktif secara default
     */
    public void addMenuItem(String label, String key, boolean isActive) {
        SidebarItem item = new SidebarItem(label, key);

        JPanel topPanel = (JPanel) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        // Cari navPanel di dalam topPanel
        for (Component comp : topPanel.getComponents()) {
            if (comp instanceof JPanel navPanel && Boolean.TRUE.equals(navPanel.getClientProperty("navPanel"))) {
                navPanel.add(item);
                navPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                break;
            }
        }

        items.add(item);
        if (isActive) setActive(item);

        item.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                setActive(item);
                if (onNavigate != null) onNavigate.accept(key);
            }
        });
        revalidate();
    }

    /** Tambah tombol logout di bawah sidebar */
    public void addLogoutButton(Runnable onLogout) {
        JPanel bottomPanel = (JPanel) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.SOUTH);

        SidebarItem logoutItem = new SidebarItem("Keluar", "logout");
        logoutItem.setNormalFg(ThemeConfig.LOGOUT_FG);
        logoutItem.setHoverBg(ThemeConfig.LOGOUT_HOVER_BG);

        bottomPanel.add(logoutItem, BorderLayout.CENTER);

        logoutItem.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int result = JOptionPane.showConfirmDialog(
                        Sidebar.this,
                        "Apakah Anda yakin ingin keluar?",
                        "Konfirmasi Logout",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION && onLogout != null) onLogout.run();
            }
        });
        revalidate();
    }

    public void setOnNavigate(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
    }

    private void setActive(SidebarItem item) {
        if (activeItem != null) activeItem.setActive(false);
        activeItem = item;
        item.setActive(true);
    }

    // ===== Inner class: SidebarItem =====
    private static class SidebarItem extends JPanel {
        private final JLabel label;
        private Color normalFg  = ThemeConfig.SIDEBAR_ITEM_FG;
        private Color hoverBg   = ThemeConfig.SIDEBAR_ITEM_HOVER_BG;
        private boolean hovered = false;
        private boolean active  = false;

        SidebarItem(String text, String key) {
            setLayout(new BorderLayout());
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(
                    ThemeConfig.SIDEBAR_ITEM_PADDING.top,
                    ThemeConfig.SIDEBAR_ITEM_PADDING.left,
                    ThemeConfig.SIDEBAR_ITEM_PADDING.bottom,
                    ThemeConfig.SIDEBAR_ITEM_PADDING.right));

            label = new JLabel(text);
            label.setFont(ThemeConfig.FONT_MEDIUM);
            label.setForeground(normalFg);
            add(label, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            });
        }

        void setActive(boolean a) {
            this.active = a;
            label.setFont(a ? ThemeConfig.FONT_SEMIBOLD : ThemeConfig.FONT_MEDIUM);
            label.setForeground(a ? ThemeConfig.SIDEBAR_ITEM_ACTIVE_FG : normalFg);
            repaint();
        }

        void setNormalFg(Color c) {
            normalFg = c;
            label.setForeground(c);
        }

        void setHoverBg(Color c) { hoverBg = c; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active) {
                g2.setColor(ThemeConfig.SIDEBAR_ITEM_ACTIVE_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                        ThemeConfig.SIDEBAR_ITEM_RADIUS, ThemeConfig.SIDEBAR_ITEM_RADIUS);
            } else if (hovered) {
                g2.setColor(hoverBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                        ThemeConfig.SIDEBAR_ITEM_RADIUS, ThemeConfig.SIDEBAR_ITEM_RADIUS);
                label.setForeground(ThemeConfig.SIDEBAR_ITEM_ACTIVE_FG);
            } else {
                label.setForeground(normalFg);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
