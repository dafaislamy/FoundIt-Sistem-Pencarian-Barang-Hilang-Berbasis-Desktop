package com.foundit.apk.view;

import com.foundit.apk.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

public class AdminVerifikasiKlaimFrame extends JFrame {
    public AdminVerifikasiKlaimFrame() {
        setTitle("Verifikasi Klaim - FoundIt Admin");
        setSize(1150, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        User adminUser = new User("ADM1", "Admin Kampus", "999", "admin@univ.ac.id", "123", "0811", Role.ADMIN);
        User pemohon = new User("U100", "Ahmad Fauzi", "220104112", "ahmad@student.ac.id", "123", "081234", Role.MAHASISWA);
        Item barangTemuan = new Item("ITM1", "Kunci Motor Yamaha", "Remote alarm & gantungan", ItemType.FIND, "Parkir Barat", LocalDate.now(), null, null);
        Claim claimTiket = new Claim("KLM-092", "Kunci Yamaha Aerox, gantungan tim Futsal Elektro", barangTemuan, pemohon);

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
            
            if (i == 5) {
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
                dispose();
                if (index == 0) new AdminDashboardFrame().setVisible(true);
                else if (index == 1) new AdminKelolaKategoriFrame().setVisible(true);
                else if (index == 2) new AdminKelolaLaporanFrame().setVisible(true);
                else if (index == 3) new AdminKelolaUserFrame().setVisible(true);
                else new AdminVerifikasiKlaimFrame().setVisible(true);
            });

            navPanel.add(btnMenu);
            navPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        }
        sidebar.add(navPanel, BorderLayout.CENTER);
        add(sidebar, BorderLayout.WEST);

        JPanel mainLayout = new JPanel(new BorderLayout(0, 20));
        mainLayout.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainLayout.setBackground(new Color(247, 249, 252));

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setOpaque(false);
        JLabel title = new JLabel("Verifikasi Klaim Barang");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        JLabel subTitle = new JLabel("Cocokkan data pengajuan klaim pemilik dengan detail barang yang ditemukan.");
        subTitle.setForeground(new Color(119, 119, 119));
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        topPanel.add(title);
        topPanel.add(subTitle);
        mainLayout.add(topPanel, BorderLayout.NORTH);

        JPanel claimCard = new JPanel(new BorderLayout(0, 20));
        claimCard.setBackground(Color.WHITE);
        claimCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            new EmptyBorder(25, 25, 25, 25)
        ));

        JPanel ticketHeader = new JPanel(new BorderLayout());
        ticketHeader.setOpaque(false);
        JLabel lblTicket = new JLabel("<html><font color='#3498db'><b>Tiket Klaim #" + claimTiket.getId() + "</b></font><br><h2 style='margin-top:6px;'>Klaim atas: " + barangTemuan.getJudul() + "</h2></html>");
        JLabel lblStatus = new JLabel(" " + claimTiket.getStatusKlaim() + " ");
        lblStatus.setForeground(new Color(214, 137, 16));
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        ticketHeader.add(lblTicket, BorderLayout.WEST);
        ticketHeader.add(lblStatus, BorderLayout.EAST);
        claimCard.add(ticketHeader, BorderLayout.NORTH);

        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        splitPanel.setOpaque(false);

        JPanel leftBox = new JPanel();
        leftBox.setLayout(new BoxLayout(leftBox, BoxLayout.Y_AXIS));
        leftBox.setOpaque(false);
        leftBox.setBorder(BorderFactory.createTitledBorder("Data Barang yang Ditemukan"));
        leftBox.add(new JLabel("<html><b>Barang:</b> " + barangTemuan.getJudul() + "<br><br>" +
                               "<b>Ditemukan di:</b> " + barangTemuan.getLokasi() + "<br><br>" +
                               "<b>Kategori:</b> Aksesoris / Kendaraan<br><br>" +
                               "<b>Catatan Penemu:</b> " + barangTemuan.getDeskripsi() + "</html>"));

        JPanel rightBox = new JPanel();
        rightBox.setLayout(new BoxLayout(rightBox, BoxLayout.Y_AXIS));
        rightBox.setOpaque(false);
        rightBox.setBorder(BorderFactory.createTitledBorder("Bukti Pengajuan Pemohon"));
        rightBox.add(new JLabel("<html><b>Nama Pengaju:</b> " + pemohon.getNama() + " (NIM: " + pemohon.getNomorIdentitas() + ")<br><br>" +
                               "<b>Tanggal Kehilangan:</b> 18 September 2026<br><br>" +
                               "<b>Ciri yang Disebutkan:</b><br>" +
                               "<div style='background-color:#f8fafc; padding:10px;'>\"" + claimTiket.getBuktiKepemilikan() + "\"</div></html>"));

        splitPanel.add(leftBox);
        splitPanel.add(rightBox);
        claimCard.add(splitPanel, BorderLayout.CENTER);

        JPanel actionFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionFooter.setOpaque(false);
        
        JButton btnTolak = new JButton("Tolak Klaim");
        btnTolak.setForeground(new Color(220, 53, 69));
        btnTolak.setFocusPainted(false);
        
        JButton btnSetuju = new JButton("Setujui & Hubungi Pemilik");
        btnSetuju.setBackground(new Color(40, 167, 69));
        btnSetuju.setForeground(Color.WHITE);
        btnSetuju.setFocusPainted(false);

        actionFooter.add(btnTolak);
        actionFooter.add(btnSetuju);
        claimCard.add(actionFooter, BorderLayout.SOUTH);

        mainLayout.add(claimCard, BorderLayout.CENTER);
        add(mainLayout);

        btnSetuju.addActionListener(e -> {
            claimTiket.approve(adminUser);
            JOptionPane.showMessageDialog(this, "Klaim berhasil disetujui!");
            dispose();
            new AdminDashboardFrame().setVisible(true);
        });

        btnTolak.addActionListener(e -> {
            claimTiket.reject(adminUser);
            JOptionPane.showMessageDialog(this, "Klaim ditolak.");
            dispose();
            new AdminDashboardFrame().setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminVerifikasiKlaimFrame().setVisible(true));
    }
}