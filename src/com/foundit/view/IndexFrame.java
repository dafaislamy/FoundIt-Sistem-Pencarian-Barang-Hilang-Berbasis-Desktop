package com.foundit.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Halaman pembuka FoundIt Desktop[cite: 58].
 */
public class IndexFrame extends JFrame {

    public IndexFrame() {
        UiKit.frame(this, "FoundIt | Temukan Barangmu", 1200, 760);
        setContentPane(build());
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiKit.BG);

        // Navigation Bar
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Color.WHITE);
        nav.setBorder(new EmptyBorder(18, 58, 18, 58));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        brand.setOpaque(false);
        brand.add(UiKit.label("Found", 30, true, ThemeConfig.HERO_GRAD_START));
        brand.add(UiKit.label("It", 30, true, new Color(0x0EA5E9)));
        nav.add(brand, BorderLayout.WEST);

        JButton loginBtn = UiKit.button("Masuk", ThemeConfig.COLOR_BRAND_DARK, Color.WHITE);
        loginBtn.setPreferredSize(new Dimension(120, 42));
        loginBtn.addActionListener(e -> openLogin());
        nav.add(loginBtn, BorderLayout.EAST);

        root.add(nav, BorderLayout.NORTH);

        // Main Content
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(ThemeConfig.BG_PAGE);

        // Hero Section (seragam dengan panel kiri LoginFrame)
        JPanel hero = ThemeConfig.createHeroGradientPanel();
        hero.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(8, 20, 8, 20);
        c.anchor = GridBagConstraints.CENTER;

        c.gridy = 0;
        hero.add(UiKit.label(
                "TEMUKAN  •  LAPORKAN  •  KEMBALI",
                13, true, ThemeConfig.COLOR_BRAND_LIGHT
        ), c);

        c.gridy++;
        hero.add(UiKit.label("Barang Hilang?", 43, true, Color.WHITE), c);

        c.gridy++;
        hero.add(UiKit.label("Temukan Kembali bersama FoundIt.", 32, true, ThemeConfig.COLOR_BRAND_LIGHT), c);

        c.gridy++;
        hero.add(UiKit.label(
                "Ruang informasi kehilangan dan penemuan barang di lingkungan kampus.",
                15, false, ThemeConfig.HERO_TEXT_SUB
        ), c);

        c.gridy++;
        JButton startBtn = UiKit.button("Mulai Sekarang", ThemeConfig.COLOR_BRAND_DARK, Color.WHITE);
        startBtn.setPreferredSize(new Dimension(205, 50));
        startBtn.setMinimumSize(new Dimension(205, 50));
        startBtn.setMaximumSize(new Dimension(205, 50));
        startBtn.addActionListener(e -> openLogin());
        hero.add(startBtn, c);

        center.add(hero, BorderLayout.CENTER);

        // Feature Cards
        JPanel lower = new JPanel(new GridLayout(1, 3, 20, 0));
        lower.setBackground(UiKit.BG);
        lower.setBorder(new EmptyBorder(25, 65, 30, 65));

        lower.add(feature("search", "Pencarian Terarah", "Cari laporan barang berdasarkan nama, kategori, dan lokasi."));
        lower.add(feature("report", "Pelaporan Mudah", "Isi formulir untuk melaporkan barang hilang atau ditemukan."));
        lower.add(feature("return", "Kembali ke Pemilik", "Bantu pemilik menemukan barangnya melalui informasi yang tepat."));

        center.add(lower, BorderLayout.SOUTH);
        root.add(center, BorderLayout.CENTER);

        return root;
    }

    private void openLogin() {
        dispose();
        new LoginFrame().setVisible(true);
    }

    private JPanel feature(String icon, String title, String desc) {
        JPanel p = UiKit.card();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel pict = new JLabel(UiKit.icon(icon, ThemeConfig.COLOR_BRAND_DARK, 34));
        p.add(pict);
        p.add(Box.createVerticalStrut(8));

        p.add(UiKit.label(title, 16, true, UiKit.TEXT));
        p.add(Box.createVerticalStrut(6));

        p.add(UiKit.label(
                "<html><div style='width:260px'>" + desc + "</div></html>",
                12, false, UiKit.MUTED
        ));
        return p;
    }
}