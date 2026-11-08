package com.foundit.apk.view;

import com.foundit.apk.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class AdminKelolaUserFrame extends JFrame {
    private List<User> userList = new ArrayList<>();
    private DefaultTableModel tableModel;

    public AdminKelolaUserFrame() {
        setTitle("Kelola Pengguna - FoundIt Admin");
        setSize(1150, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        userList.add(new User("U1", "Budi Santoso", "220101002", "budi@campus.ac.id", "pass", "081234567890", Role.MAHASISWA));
        userList.add(new User("U2", "Sarah Amelia", "230102045", "sarah@campus.ac.id", "pass", "089876543211", Role.MAHASISWA));
        userList.add(new User("U3", "Dimas Anggara", "210103099", "dimas@campus.ac.id", "pass", "085211223344", Role.MAHASISWA));

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
            
            if (i == 3) {
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

        JPanel mainLayout = new JPanel(new BorderLayout(0, 20));
        mainLayout.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainLayout.setBackground(new Color(247, 249, 252));

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setOpaque(false);
        JLabel title = new JLabel("Kelola Pengguna");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        JLabel subTitle = new JLabel("Manajemen data akun mahasiswa dan pengguna aplikasi FoundIt.");
        subTitle.setForeground(new Color(119, 119, 119));
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        topPanel.add(title);
        topPanel.add(subTitle);
        mainLayout.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);

        JPanel controlCard = new JPanel(new GridLayout(1, 4, 12, 0));
        controlCard.setBackground(Color.WHITE);
        controlCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        controlCard.setMaximumSize(new Dimension(2000, 75));

        JTextField tfSearch = new JTextField();
        JComboBox<String> cbFakultas = new JComboBox<>(new String[]{"Semua Fakultas", "Fakultas Teknik", "Fakultas Ekonomi", "Fakultas Ilmu Komputer"});
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Semua Status", "Aktif", "Nonaktif"});
        
        JButton btnTambah = new JButton("+ Tambah");
        btnTambah.setBackground(new Color(52, 152, 219));
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFocusPainted(false);

        controlCard.add(tfSearch);
        controlCard.add(cbFakultas);
        controlCard.add(cbStatus);
        controlCard.add(btnTambah);

        centerPanel.add(controlCard, BorderLayout.NORTH);

        String[] columns = {"NIM & Nama", "Fakultas / Prodi", "Kontak Email & Telp", "Status Akun", "Aksi"};
        tableModel = new DefaultTableModel(columns, 0);
        refreshTable();

        JTable table = new JTable(tableModel);
        table.setRowHeight(60);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnTambah.addActionListener(e -> {
            JTextField namaField = new JTextField();
            JTextField nimField = new JTextField();
            JTextField emailField = new JTextField();
            JTextField telpField = new JTextField();
            Object[] message = {
                "Nama Lengkap:", namaField,
                "NIM:", nimField,
                "Email:", emailField,
                "No Telepon:", telpField
            };
            int option = JOptionPane.showConfirmDialog(null, message, "Tambah Pengguna Baru", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                if (!namaField.getText().trim().isEmpty() && !nimField.getText().trim().isEmpty()) {
                    userList.add(new User("U" + (userList.size() + 1), namaField.getText(), nimField.getText(), emailField.getText(), "pass", telpField.getText(), Role.MAHASISWA));
                    refreshTable();
                    JOptionPane.showMessageDialog(null, "Pengguna baru berhasil ditambahkan!");
                } else {
                    JOptionPane.showMessageDialog(null, "Nama dan NIM wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (col == 4 && row >= 0) {
                    String[] options = {"Ubah Status (Aktif/Nonaktif)", "Hapus User", "Batal"};
                    int choice = JOptionPane.showOptionDialog(null, "Pilih aksi untuk pengguna ini:", "Kelola User",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                    if (choice == 0) {
                        JOptionPane.showMessageDialog(null, "Status akun pengguna berhasil diubah!");
                    } else if (choice == 1) {
                        int confirm = JOptionPane.showConfirmDialog(null, "Yakin ingin menghapus pengguna ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            userList.remove(row);
                            refreshTable();
                            JOptionPane.showMessageDialog(null, "Pengguna berhasil dihapus!");
                        }
                    }
                }
            }
        });

        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainLayout.add(centerPanel, BorderLayout.CENTER);
        add(mainLayout);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (User u : userList) {
            tableModel.addRow(new Object[]{
                "<html><b>" + u.getNama() + "</b><br><font color='#777'>NIM: " + u.getNomorIdentitas() + "</font></html>",
                "Fakultas Ilmu Komputer<br><font color='#777'>Informatika (2025)</font>",
                u.getEmail() + "<br><font color='#777'>" + u.getNoTelp() + "</font>",
                "Aktif",
                "<html><font color='#3498db'><u>Nonaktifkan | Hapus</u></font></html>"
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminKelolaUserFrame().setVisible(true));
    }
}