package com.foundit.apk.view;

import com.foundit.apk.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdminKelolaLaporanFrame extends JFrame {
    private List<Item> laporanList = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable table;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public AdminKelolaLaporanFrame() {
        setTitle("Kelola Laporan Barang - FoundIt Admin");
        setSize(1150, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Category katElektronik = new Category("Elektronik");
        Category katAksesoris = new Category("Aksesoris");
        User pelapor1 = new User("U1", "Budi Santoso", "220101002", "budi@campus.ac.id", "123", "081234567890", Role.MAHASISWA);
        User pelapor2 = new User("U2", "Sarah Amelia", "230102045", "sarah@campus.ac.id", "123", "089876543211", Role.MAHASISWA);

        Item item1 = new Item("I1", "Flashdisk SanDisk 64GB", "Warna merah hitam", ItemType.LOST, "Gedung C Fakultas Teknik", LocalDate.now(), katElektronik, pelapor1);
        item1.updateStatus(ItemStatus.PUBLISHED);

        Item item2 = new Item("I2", "Kunci Motor Yamaha", "Gantungan kulit coklat", ItemType.FIND, "Area Parkir Timur", LocalDate.now(), katAksesoris, pelapor2);
        item2.updateStatus(ItemStatus.PUBLISHED);

        laporanList.add(item1);
        laporanList.add(item2);

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
            
            if (i == 2) {
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
                else if (index == 3) new AdminKelolaUserFrame().setVisible(true);
                else if (index == 5) new AdminVerifikasiKlaimFrame().setVisible(true);
                else new AdminKelolaLaporanFrame().setVisible(true);
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
        JLabel title = new JLabel("Kelola Laporan Barang");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        JLabel subTitle = new JLabel("Daftar seluruh laporan barang yang terdaftar di dalam sistem beserta statusnya.");
        subTitle.setForeground(new Color(119, 119, 119));
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        topPanel.add(title);
        topPanel.add(subTitle);
        mainLayout.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);

        JPanel filterCard = new JPanel(new GridLayout(1, 4, 12, 0));
        filterCard.setBackground(Color.WHITE);
        filterCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        filterCard.setMaximumSize(new Dimension(2000, 75));

        JTextField tfSearch = new JTextField();
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Semua Status", "Aktif", "Proses Klaim", "Dikembalikan"});
        JComboBox<String> cbKategori = new JComboBox<>(new String[]{"Semua Kategori", "Elektronik", "Dokumen", "Aksesoris", "Lainnya"});
        
        JButton btnReset = new JButton("Reset Filter");
        btnReset.setBackground(Color.WHITE);
        btnReset.setFocusPainted(false);

        filterCard.add(tfSearch);
        filterCard.add(cbStatus);
        filterCard.add(cbKategori);
        filterCard.add(btnReset);

        centerPanel.add(filterCard, BorderLayout.NORTH);

        String[] columns = {"Barang", "Kategori", "Jenis Laporan", "Lokasi Kejadian", "Status Saat Ini", "Aksi"};
        tableModel = new DefaultTableModel(columns, 0);
        refreshTable();

        table = new JTable(tableModel);
        table.setRowHeight(60);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        tfSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            
            private void filterTable() {
                String text = tfSearch.getText();
                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        btnReset.addActionListener(e -> {
            tfSearch.setText("");
            cbStatus.setSelectedIndex(0);
            cbKategori.setSelectedIndex(0);
            rowSorter.setRowFilter(null);
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (col == 5 && row >= 0) {
                    int modelRow = table.convertRowIndexToModel(row);
                    String[] newStatuses = {"Aktif", "Proses Klaim", "Dikembalikan", "Ditutup"};
                    String selected = (String) JOptionPane.showInputDialog(null, "Pilih Status Baru:", "Ubah Status Barang",
                            JOptionPane.QUESTION_MESSAGE, null, newStatuses, newStatuses[0]);
                    if (selected != null) {
                        laporanList.get(modelRow).updateStatus(ItemStatus.COMPLETED);
                        JOptionPane.showMessageDialog(null, "Status barang berhasil diperbarui menjadi: " + selected);
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
        for (Item item : laporanList) {
            String katName = (item.getCategory() != null) ? item.getCategory().getNamaKategori() : "-";
            tableModel.addRow(new Object[]{
                "<html><b>" + item.getJudul() + "</b><br><font color='#777'>" + item.getDeskripsi() + "</font></html>",
                katName,
                item.getTipe(),
                item.getLokasi(),
                "Aktif",
                "<html><font color='#3498db'><u>Ubah Status</u></font></html>"
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminKelolaLaporanFrame().setVisible(true));
    }
}