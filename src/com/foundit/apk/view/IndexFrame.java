
package com.foundit.apk.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Halaman pembuka FoundIt Desktop.
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

        JPanel brand = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 0, 0)
        );
        brand.setOpaque(false);

        brand.add(UiKit.label(
            "Found", 30, true, UiKit.BLUE_DARK
        ));

        brand.add(UiKit.label(
            "It", 30, true, UiKit.BLUE
        ));

        nav.add(brand, BorderLayout.WEST);

        JButton login = UiKit.button(
            "Masuk", UiKit.BLUE, Color.WHITE
        );

        login.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        nav.add(login, BorderLayout.EAST);
        root.add(nav, BorderLayout.NORTH);

        // Main Content
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(UiKit.BG);

        // Hero Section
        JPanel hero = UiKit.gradient(
            UiKit.BLUE, UiKit.BLUE_DARK
        );

        hero.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(8, 20, 8, 20);
        c.anchor = GridBagConstraints.CENTER;

        c.gridy = 0;
        hero.add(UiKit.label(
            "TEMUKAN  •  LAPORKAN  •  KEMBALI",
            13,
            true,
            new Color(220, 243, 255)
        ), c);

        c.gridy++;
        hero.add(UiKit.label(
            "Barang Hilang?",
            43,
            true,
            Color.WHITE
        ), c);

        c.gridy++;
        hero.add(UiKit.label(
            "Temukan Kembali bersama FoundIt.",
            32,
            true,
            UiKit.SKY
        ), c);

        c.gridy++;
        hero.add(UiKit.label(
            "Ruang informasi kehilangan dan penemuan barang di lingkungan kampus.",
            15,
            false,
            new Color(238, 247, 253)
        ), c);

        // Start Button
        c.gridy++;

        JButton start = UiKit.button(
            "Mulai Sekarang", UiKit.GREEN, Color.WHITE
        );

        start.setPreferredSize(new Dimension(205, 50));
        start.setMinimumSize(new Dimension(205, 50));
        start.setMaximumSize(new Dimension(205, 50));

        start.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        hero.add(start, c);

        center.add(hero, BorderLayout.CENTER);

        // Feature Cards
        JPanel lower = new JPanel(
            new GridLayout(1, 3, 20, 0)
        );

        lower.setBackground(UiKit.BG);
        lower.setBorder(
            new EmptyBorder(25, 65, 30, 65)
        );

        lower.add(feature(
            "search",
            "Pencarian Terarah",
            "Cari laporan barang berdasarkan nama, kategori, dan lokasi."
        ));

        lower.add(feature(
            "report",
            "Pelaporan Mudah",
            "Isi formulir untuk melaporkan barang hilang atau ditemukan."
        ));

        lower.add(feature(
            "return",
            "Kembali ke Pemilik",
            "Bantu pemilik menemukan barangnya melalui informasi yang tepat."
        ));

        center.add(lower, BorderLayout.SOUTH);
        root.add(center, BorderLayout.CENTER);

        return root;
    }

    // Feature Card
    private JPanel feature(
        String icon,
        String title,
        String desc
    ) {
        JPanel p = UiKit.card();

        p.setLayout(
            new BoxLayout(p, BoxLayout.Y_AXIS)
        );

        p.setBorder(
            new EmptyBorder(20, 22, 20, 22)
        );

        JLabel pict = new JLabel(
            UiKit.icon(icon, UiKit.BLUE, 34)
        );

        p.add(pict);
        p.add(Box.createVerticalStrut(8));

        p.add(UiKit.label(
            title, 16, true, UiKit.TEXT
        ));

        p.add(Box.createVerticalStrut(6));

        p.add(UiKit.label(
            "<html><div style='width:260px'>"
                + desc
                + "</div></html>",
            12,
            false,
            UiKit.MUTED
        ));

        return p;
    }
}
