package com.foundit.apk.view;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class AdminUbahStatusFrame extends JFrame {
    private List<String[]> barangList = new ArrayList<>();
    private List<Integer> displayedIndices = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTextField tfSearch;
    private JComboBox<String> cbStatus;

    public AdminUbahStatusFrame() {
        setTitle("Ubah Status Barang - FoundIt Admin");
        setSize(1150, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Data awal sesuai dengan tabel pada admin-ubah-status.html
        barangList.add(new String[]{"#BRG-1029", "Flashdisk SanDisk 64GB", "Elektronik", "Rian Pratama", "Teknik Mesin", "Gedung C Fakultas Teknik", "Hilang"});
        barangList.add(new String[]{"#BRG-1034", "Kunci Motor Yamaha Aerox", "Aksesoris", "Budi Santoso", "Teknik Elektro", "Parkir Timur Kampus", "Ditemukan"});
        barangList.add(new String[]{"#BRG-0992", "Dompet Kulit Hitam", "Aksesoris", "Sarah Amelia", "Sistem Informasi", "Perpustakaan Lt. 2", "Dalam Proses Klaim"});
        barangList.add(new String[]{"#BRG-0871", "KTM atas nama Dimas A.", "Dokumen", "Petugas Satpam Rektorat", "Staff", "Lobi Rektorat", "Telah Dikembalikan"});

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
            
            // Highlight menu ke-5 (indeks 4): "Ubah Status Barang"
            if (i == 4) {
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
        JPanel mainLayout = new JPanel(new BorderLayout(0, 20));
        mainLayout.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainLayout.setBackground(new Color(247, 249, 252));

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setOpaque(false);
        JLabel title = new JLabel("Ubah Status Barang");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        JLabel subTitle = new JLabel("Perbarui status siklus barang (Hilang, Ditemukan, Proses Klaim, atau Selesai Dikembalikan).");
        subTitle.setForeground(new Color(119, 119, 119));
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        topPanel.add(title);
        topPanel.add(subTitle);
        mainLayout.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);

        // KARTU FILTER & PENCARIAN BARANG
        JPanel controlCard = new JPanel(new GridLayout(1, 2, 15, 0));
        controlCard.setBackground(Color.WHITE);
        controlCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        controlCard.setMaximumSize(new Dimension(2000, 85));

        JPanel searchPanel = new JPanel(new BorderLayout(0, 5));
        searchPanel.setOpaque(false);
        JLabel lblSearch = new JLabel("Pencarian Barang");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tfSearch = new JTextField();
        tfSearch.setToolTipText("Cari nama barang, pelapor, atau kode laporan (#BRG)...");
        searchPanel.add(lblSearch, BorderLayout.NORTH);
        searchPanel.add(tfSearch, BorderLayout.CENTER);

        JPanel filterPanel = new JPanel(new BorderLayout(0, 5));
        filterPanel.setOpaque(false);
        JLabel lblFilter = new JLabel("Filter Status Saat Ini");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cbStatus = new JComboBox<>(new String[]{
            "Semua Status",
            "Hilang (Aktif)",
            "Ditemukan (Tersimpan di Posko)",
            "Dalam Proses Klaim",
            "Telah Dikembalikan / Selesai"
        });
        filterPanel.add(lblFilter, BorderLayout.NORTH);
        filterPanel.add(cbStatus, BorderLayout.CENTER);

        controlCard.add(searchPanel);
        controlCard.add(filterPanel);
        centerPanel.add(controlCard, BorderLayout.NORTH);

        // TABEL KONTROL STATUS BARANG
        String[] columns = {"Barang", "Kategori", "Pelapor / Penemu", "Lokasi Terakhir", "Status Saat Ini", "Tindakan"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        refreshTable();

        JTable table = new JTable(tableModel);
        table.setRowHeight(60);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Event Pencarian & Filter Otomatis (Konversi dari jalankanFilterUniversal di admin.js)
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshTable(); }
            public void removeUpdate(DocumentEvent e) { refreshTable(); }
            public void changedUpdate(DocumentEvent e) { refreshTable(); }
        });
        cbStatus.addActionListener(e -> refreshTable());

        // Event Klik Kolom Tindakan (Konversi dari #modalUpdateStatus di HTML & admin.js)
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (col == 5 && row >= 0) {
                    int actualIndex = displayedIndices.get(row);
                    String[] item = barangList.get(actualIndex);

                    JTextField tfTargetBarang = new JTextField(item[1] + " (" + item[0] + ")");
                    tfTargetBarang.setEditable(false);
                    tfTargetBarang.setBackground(new Color(240, 240, 240));

                    String[] opsiStatus = {
                        "Hilang (Masih Dicari)",
                        "Ditemukan (Tersimpan di Pos Keamanan/Admin)",
                        "Dalam Proses Verifikasi Klaim",
                        "Telah Dikembalikan (Klaim Berhasil / Selesai)",
                        "Dibatalkan / Kedaluwarsa"
                    };
                    JComboBox<String> cbStatusBaru = new JComboBox<>(opsiStatus);

                    String currentStatus = item[6].toLowerCase();
                    if (currentStatus.contains("hilang")) cbStatusBaru.setSelectedIndex(0);
                    else if (currentStatus.contains("ditemukan")) cbStatusBaru.setSelectedIndex(1);
                    else if (currentStatus.contains("klaim")) cbStatusBaru.setSelectedIndex(2);
                    else if (currentStatus.contains("dikembalikan") || currentStatus.contains("selesai")) cbStatusBaru.setSelectedIndex(3);
                    else if (currentStatus.contains("batal")) cbStatusBaru.setSelectedIndex(4);

                    JTextField tfPenerima = new JTextField();
                    JTextArea taCatatan = new JTextArea(3, 20);
                    taCatatan.setLineWrap(true);
                    taCatatan.setWrapStyleWord(true);
                    JScrollPane scrollCatatan = new JScrollPane(taCatatan);

                    Object[] message = {
                        "Informasi Barang:", tfTargetBarang,
                        "Pilih Status Baru:", cbStatusBaru,
                        "Pihak Penerima / Pengambil (Opsional):", tfPenerima,
                        "Catatan / Berita Acara Penyerahan:", scrollCatatan
                    };

                    int option = JOptionPane.showConfirmDialog(
                        null, message, "Formulir Pembaruan Status Barang", JOptionPane.OK_CANCEL_OPTION
                    );

                    if (option == JOptionPane.OK_OPTION) {
                        String selectedText = (String) cbStatusBaru.getSelectedItem();
                        // Memotong teks sebelum tanda kurung '(' sesuai logika split("(")[0].trim() di admin.js
                        String statusBersih = selectedText != null ? selectedText.split("\\(")[0].trim() : "Diperbarui";
                        item[6] = statusBersih;
                        refreshTable();
                        JOptionPane.showMessageDialog(null, "Status barang berhasil diperbarui!");
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
        displayedIndices.clear();

        String keyword = tfSearch != null ? tfSearch.getText().trim().toLowerCase() : "";
        String filterVal = "";
        if (cbStatus != null && cbStatus.getSelectedIndex() > 0) {
            int idx = cbStatus.getSelectedIndex();
            if (idx == 1) filterVal = "hilang";
            else if (idx == 2) filterVal = "ditemukan";
            else if (idx == 3) filterVal = "klaim";
            else if (idx == 4) filterVal = "dikembalikan";
        }

        for (int i = 0; i < barangList.size(); i++) {
            String[] b = barangList.get(i);
            String rowText = (b[0] + " " + b[1] + " " + b[2] + " " + b[3] + " " + b[4] + " " + b[5] + " " + b[6]).toLowerCase();

            boolean matchKeyword = keyword.isEmpty() || rowText.contains(keyword);
            boolean matchFilter = filterVal.isEmpty() || b[6].toLowerCase().contains(filterVal);

            if (matchKeyword && matchFilter) {
                displayedIndices.add(i);

                String statusColor = "#3498db";
                String sLower = b[6].toLowerCase();
                if (sLower.contains("hilang")) statusColor = "#d68910";
                else if (sLower.contains("ditemukan") || sLower.contains("dikembalikan")) statusColor = "#27ae60";
                else if (sLower.contains("batal")) statusColor = "#7f8c8d";

                String actionLabel = sLower.contains("dikembalikan") ? "Ubah Lagi" : "Perbarui Status";

                tableModel.addRow(new Object[]{
                    "<html><b>" + b[1] + "</b><br><font color='#777'>ID: " + b[0] + "</font></html>",
                    b[2],
                    "<html>" + b[3] + "<br><font color='#777'>" + b[4] + "</font></html>",
                    b[5],
                    "<html><font color='" + statusColor + "'><b>" + b[6] + "</b></font></html>",
                    "<html><font color='#3498db'><u>" + actionLabel + "</u></font></html>"
                });
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminUbahStatusFrame().setVisible(true));
    }
}