package com.foundit.view;

import com.foundit.config.DatabaseConnection;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    public MainFrame() {
        super("FoundIt: Pencarian Barang Hilang");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(createHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Barang", new ItemPanel());
        tabs.addTab("Klaim", new ClaimPanel());
        tabs.addTab("Kategori", new CategoryPanel());
        tabs.addTab("Pengguna", new UserPanel());
        tabs.addTab("Admin", new AdminPanel());
        // Muat ulang data & combo relasi setiap kali tab dibuka
        tabs.addChangeListener(e -> {
            Component c = tabs.getSelectedComponent();
            if (c instanceof AbstractCrudPanel<?> panel) {
                panel.onShow();
            }
        });
        add(tabs, BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        setMinimumSize(new Dimension(980, 600));
        setSize(1240, 720);
        setLocationRelativeTo(null);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x0F766E));
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JLabel title = new JLabel("FoundIt: Pencarian Barang Hilang");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        JLabel subtitle = new JLabel("Catat barang hilang/ditemukan, proses klaim kepemilikan, kelola akun");
        subtitle.setForeground(new Color(0xCCFBF1));
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        footer.add(new JLabel("Terhubung ke " + DatabaseConnection.getDisplayUrl()), BorderLayout.WEST);
        return footer;
    }
}
