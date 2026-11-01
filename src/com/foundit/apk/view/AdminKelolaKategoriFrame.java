package com.foundit.apk.view;

import com.foundit.apk.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AdminKelolaKategoriFrame extends JFrame {
    private List<Category> kategoriList = new ArrayList<>();
    private DefaultTableModel tableModel;

    public AdminKelolaKategoriFrame() {
        setTitle("Kelola Kategori - FoundIt Admin");
        setSize(1150, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        kategoriList.add(new Category("Elektronik"));
        kategoriList.add(new Category("Aksesoris"));
        kategoriList.add(new Category("Dokumen"));
        kategoriList.add(new Category("Lainnya"));

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
            
            if (i == 1) {
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
                else if (index == 2) new AdminKelolaLaporanFrame().setVisible(true);
                else if (index == 3) new AdminKelolaUserFrame().setVisible(true);
                else if (index == 5) new AdminVerifikasiKlaimFrame().setVisible(true);
                else new AdminKelolaKategoriFrame().setVisible(true);
            });

            navPanel.add(btnMenu);
            navPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        }
        sidebar.add(navPanel, BorderLayout.CENTER);
        add(sidebar, BorderLayout.WEST);

        JPanel mainLayout = new JPanel(new BorderLayout(0, 25));
        mainLayout.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainLayout.setBackground(new Color(247, 249, 252));

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setOpaque(false);
        JLabel title = new JLabel("Kelola Kategori Barang");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        JLabel subTitle = new JLabel("Atur kategori barang untuk mempermudah pencarian dan filter pengguna.");
        subTitle.setForeground(new Color(119, 119, 119));
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        topPanel.add(title);
        topPanel.add(subTitle);
        mainLayout.add(topPanel, BorderLayout.NORTH);

        JPanel contentGrid = new JPanel(new GridLayout(1, 2, 25, 0));
        contentGrid.setOpaque(false);

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel formTitle = new JLabel("Tambah Kategori Baru");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formCard.add(formTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        formCard.add(new JLabel("Nama Kategori"));
        formCard.add(Box.createRigidArea(new Dimension(0, 8)));
        JTextField tfNama = new JTextField();
        tfNama.setMaximumSize(new Dimension(2000, 38));
        formCard.add(tfNama);
        formCard.add(Box.createRigidArea(new Dimension(0, 15)));

        formCard.add(new JLabel("Keterangan / Deskripsi"));
        formCard.add(Box.createRigidArea(new Dimension(0, 8)));
        JTextArea taDesc = new JTextArea(4, 20);
        taDesc.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        formCard.add(new JScrollPane(taDesc));
        formCard.add(Box.createRigidArea(new Dimension(0, 25)));

        JButton btnSimpan = new JButton("Simpan Kategori");
        btnSimpan.setBackground(new Color(52, 152, 219));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFocusPainted(false);
        btnSimpan.setMaximumSize(new Dimension(2000, 42));
        formCard.add(btnSimpan);

        contentGrid.add(formCard);

        String[] columns = {"Nama Kategori & Deskripsi", "Aksi"};
        tableModel = new DefaultTableModel(columns, 0);
        refreshTable();

        JTable table = new JTable(tableModel);
        table.setRowHeight(60);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        JScrollPane tableScroll = new JScrollPane(table);
        contentGrid.add(tableScroll);

        mainLayout.add(contentGrid, BorderLayout.CENTER);
        add(mainLayout);

        btnSimpan.addActionListener(e -> {
            String nama = tfNama.getText().trim();
            if (!nama.isEmpty()) {
                kategoriList.add(new Category(nama));
                refreshTable();
                tfNama.setText("");
                taDesc.setText("");
                JOptionPane.showMessageDialog(this, "Kategori berhasil disimpan!");
            } else {
                JOptionPane.showMessageDialog(this, "Nama kategori wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (col == 1 && row >= 0) {
                    String[] options = {"Edit", "Hapus", "Batal"};
                    int choice = JOptionPane.showOptionDialog(null, "Pilih aksi untuk kategori ini:", "Kelola Kategori",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                    if (choice == 0) {
                        String baru = JOptionPane.showInputDialog(null, "Ubah nama kategori:", kategoriList.get(row).getNamaKategori());
                        if (baru != null && !baru.trim().isEmpty()) {
                            kategoriList.get(row).setNamaKategori(baru.trim());
                            refreshTable();
                            JOptionPane.showMessageDialog(null, "Kategori berhasil diperbarui!");
                        }
                    } else if (choice == 1) {
                        int confirm = JOptionPane.showConfirmDialog(null, "Yakin ingin menghapus kategori ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            kategoriList.remove(row);
                            refreshTable();
                            JOptionPane.showMessageDialog(null, "Kategori berhasil dihapus!");
                        }
                    }
                }
            }
        });
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Category c : kategoriList) {
            String desc = "Handphone, Laptop, Flashdisk, Charger";
            if (c.getNamaKategori().equals("Aksesoris")) desc = "Kunci kendaraan, Dompet, Jam Tangan";
            else if (c.getNamaKategori().equals("Dokumen")) desc = "KTM, KTP, SIM, Buku Catatan";
            else if (c.getNamaKategori().equals("Lainnya")) desc = "Payung, Jaket, Tumbler";
            
            tableModel.addRow(new Object[]{
                "<html><b>" + c.getNamaKategori() + "</b><br><font color='#777'>" + desc + "</font></html>",
                "<html><font color='#3498db'><u>Edit | Hapus</u></font></html>"
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminKelolaKategoriFrame().setVisible(true));
    }
}