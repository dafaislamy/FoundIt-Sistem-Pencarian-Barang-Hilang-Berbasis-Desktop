package com.foundit.apk.view;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AdminVerifikasiLaporanFrame extends JFrame {
    private List<String[]> laporanList = new ArrayList<>();
    private JPanel cardsContainer;

    public AdminVerifikasiLaporanFrame() {
        setTitle("Verifikasi Laporan - FoundIt Admin");
        setSize(1150, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Data awal sesuai dengan kartu review laporan di admin-verifikasi-laporan.html
        // Format: {Tipe, Waktu, Judul Barang, Pelapor, Info Nim/Fakultas, Kategori, Label Lokasi, Lokasi, Deskripsi, StatusDisetujui}
        laporanList.add(new String[]{
            "Laporan Kehilangan", "Diajukan 2 jam yang lalu", "MacBook Air M2 2023",
            "Sarah Amelia", "NIM: 230102045 - FASILKOM", "Elektronik", "Lokasi Hilang:", "Kantin Utama",
            "Warna Space Grey, ada goresan tipis di bagian bawah pojok kanan dan stiker GitHub.", "false"
        });
        laporanList.add(new String[]{
            "Laporan Ditemukan", "Diajukan 4 jam yang lalu", "Kunci Motor Honda Vario",
            "Budi Santoso", "NIM: 220101002 - FT", "Aksesoris", "Lokasi Ditemukan:", "Parkiran Gedung C",
            "Kunci kontak remote hitam dengan gantungan tali merah bertuliskan 'One Heart'.", "false"
        });

        // SIDEBAR NAVIGASI
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
            
            // Highlight menu ke-7 (indeks 6): "Verifikasi Laporan"
            if (i == 6) {
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

        // KONTEN UTAMA
        JPanel mainLayout = new JPanel(new BorderLayout(0, 25));
        mainLayout.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainLayout.setBackground(new Color(247, 249, 252));

        // TOPBAR (Judul & Profil Admin)
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("Verifikasi Laporan Masuk");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(44, 62, 80));
        JLabel subTitle = new JLabel("Validasi laporan barang yang diajukan oleh pengguna sebelum ditayangkan ke publik.");
        subTitle.setForeground(new Color(119, 119, 119));
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titlePanel.add(title);
        titlePanel.add(subTitle);

        topBar.add(titlePanel, BorderLayout.WEST);
        mainLayout.add(topBar, BorderLayout.NORTH);

        // CONTAINER KARTU REVIEW LAPORAN (Grid 2 Kolom seperti .col-lg-6 di Bootstrap)
        cardsContainer = new JPanel(new GridLayout(1, 2, 20, 20));
        cardsContainer.setOpaque(false);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(cardsContainer, BorderLayout.NORTH);

        refreshCards();

        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        mainLayout.add(scrollPane, BorderLayout.CENTER);
        add(mainLayout, BorderLayout.CENTER);
    }

    private void refreshCards() {
        cardsContainer.removeAll();

        if (laporanList.isEmpty()) {
            cardsContainer.setLayout(new BorderLayout());
            JLabel emptyLabel = new JLabel("Tidak ada laporan baru yang perlu diverifikasi.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLabel.setForeground(new Color(136, 136, 136));
            emptyLabel.setBorder(new EmptyBorder(50, 0, 0, 0));
            cardsContainer.add(emptyLabel, BorderLayout.CENTER);
        } else {
            int cols = 2;
            int rows = (int) Math.ceil(laporanList.size() / 2.0);
            cardsContainer.setLayout(new GridLayout(rows, cols, 20, 20));

            for (int i = 0; i < laporanList.size(); i++) {
                String[] item = laporanList.get(i);
                cardsContainer.add(createReportCard(item, i));
            }

            // Jika jumlah kartu ganjil, tambahkan panel kosong agar lebar kartu tetap setengah layar (col-lg-6)
            if (laporanList.size() % 2 != 0) {
                JPanel dummy = new JPanel();
                dummy.setOpaque(false);
                cardsContainer.add(dummy);
            }
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private JPanel createReportCard(String[] item, int index) {
        boolean isApproved = Boolean.parseBoolean(item[9]);

        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(isApproved ? new Color(248, 250, 252) : Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(22, 22, 22, 22)
        ));

        // 1. Bagian Header Kartu (Badge & Waktu)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel badge = new JLabel(item[0]);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(5, 12, 5, 12));

        // Warna sesuai dengan admin_3.css (.badge.bg-danger & .badge.bg-warning)
        if (item[0].toLowerCase().contains("kehilangan")) {
            badge.setBackground(new Color(255, 244, 229)); // #fff4e5
            badge.setForeground(new Color(214, 137, 16));  // #d68910
        } else {
            badge.setBackground(new Color(234, 248, 239)); // #eaf8ef
            badge.setForeground(new Color(39, 174, 96));   // #27ae60
        }

        JLabel timeLabel = new JLabel(item[1]);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setForeground(new Color(136, 136, 136));

        headerPanel.add(badge, BorderLayout.WEST);
        headerPanel.add(timeLabel, BorderLayout.EAST);

        // 2. Bagian Tengah (Judul Barang, Pelapor, & Kotak Abu-abu Detail)
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setOpaque(false);

        JLabel itemTitle = new JLabel(item[2]);
        itemTitle.setFont(new Font("Segoe UI", Font.BOLD, 19));
        itemTitle.setForeground(isApproved ? new Color(130, 140, 150) : new Color(44, 62, 80));
        itemTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel reporterLabel = new JLabel("<html><font color='#6c757d'>Oleh: <b>" + item[3] + "</b> (" + item[4] + ")</font></html>");
        reporterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        reporterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        bodyPanel.add(itemTitle);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        bodyPanel.add(reporterLabel);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 14)));

        // Kotak Detail (.bg-light)
        JPanel detailBox = new JPanel(new BorderLayout(0, 8));
        detailBox.setBackground(new Color(248, 249, 250));
        detailBox.setBorder(new EmptyBorder(14, 14, 14, 14));
        detailBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel metaGrid = new JPanel(new GridLayout(1, 2, 10, 0));
        metaGrid.setOpaque(false);
        JLabel catLabel = new JLabel("<html><b>Kategori:</b> " + item[5] + "</html>");
        catLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JLabel locLabel = new JLabel("<html><b>" + item[6] + "</b> " + item[7] + "</html>");
        locLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        metaGrid.add(catLabel);
        metaGrid.add(locLabel);

        JLabel descLabel = new JLabel("<html><b>Deskripsi / Ciri:</b><br><font color='#6c757d'>" + item[8] + "</font></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        detailBox.add(metaGrid, BorderLayout.NORTH);
        detailBox.add(descLabel, BorderLayout.CENTER);
        bodyPanel.add(detailBox);

        // 3. Bagian Footer Tombol Aksi
        JPanel footerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        footerPanel.setOpaque(false);

        JButton btnTolak = new JButton("Tolak Laporan");
        btnTolak.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTolak.setBackground(Color.WHITE);
        btnTolak.setForeground(new Color(231, 76, 60)); // Merah #e74c3c
        btnTolak.setBorder(BorderFactory.createLineBorder(new Color(231, 76, 60), 1));
        btnTolak.setFocusPainted(false);
        btnTolak.setPreferredSize(new Dimension(0, 38));

        JButton btnSetuju = new JButton(isApproved ? "Telah Dipublikasikan ✓" : "Setujui & Publikasikan");
        btnSetuju.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSetuju.setBackground(isApproved ? new Color(149, 165, 166) : new Color(52, 152, 219)); // Biru #3498db
        btnSetuju.setForeground(Color.WHITE);
        btnSetuju.setFocusPainted(false);
        btnSetuju.setBorderPainted(false);
        btnSetuju.setPreferredSize(new Dimension(0, 38));

        // Jika sudah disetujui, nonaktifkan tombol (konversi dari pointerEvents = "none" & opacity = "0.6" di admin_3.js)
        if (isApproved) {
            btnTolak.setEnabled(false);
            btnTolak.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            btnSetuju.setEnabled(false);
        }

        // Event Tombol Tolak Laporan (sesuai prompt alasan penolakan di admin_3.js)
        btnTolak.addActionListener(e -> {
            String alasan = JOptionPane.showInputDialog(
                this,
                "Masukkan alasan penolakan laporan:",
                "Tolak Laporan",
                JOptionPane.QUESTION_MESSAGE
            );
            if (alasan != null && !alasan.trim().isEmpty()) {
                laporanList.remove(index);
                refreshCards();
                JOptionPane.showMessageDialog(this, "⚠️ Laporan telah ditolak.", "Notifikasi", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Event Tombol Setujui & Publikasikan (sesuai confirm dialog di admin_3.js)
        btnSetuju.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Setujui laporan ini agar dapat dilihat oleh mahasiswa?",
                "Konfirmasi Publikasi",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                item[9] = "true";
                refreshCards();
                JOptionPane.showMessageDialog(this, "✅ Laporan berhasil disetujui & dipublikasikan!", "Notifikasi", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        footerPanel.add(btnTolak);
        footerPanel.add(btnSetuju);

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(bodyPanel, BorderLayout.CENTER);
        card.add(footerPanel, BorderLayout.SOUTH);

        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminVerifikasiLaporanFrame().setVisible(true));
    }
}