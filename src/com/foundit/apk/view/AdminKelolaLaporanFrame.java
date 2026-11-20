package com.foundit.apk.view;

import com.foundit.apk.controller.AdminKelolaLaporanController;
import com.foundit.apk.model.Item;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Kelola Laporan Barang - FoundIt Admin (View murni).
 */
public class AdminKelolaLaporanFrame extends JFrame {

    private DefaultTableModel tableModel;
    private JTable table;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField tfSearch;
    private JComboBox<String> cbStatus;
    private JComboBox<String> cbKategori;
    private JButton btnReset;
    private Consumer<Integer> menuNavigationHandler;

    public AdminKelolaLaporanFrame() {
        this(true);
    }

    public AdminKelolaLaporanFrame(boolean bindDefaultController) {
        setTitle("Kelola Laporan Barang - FoundIt Admin");
        setSize(1150, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

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
                if (menuNavigationHandler != null) {
                    menuNavigationHandler.accept(index);
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

        tfSearch = new JTextField();
        cbStatus = new JComboBox<>(new String[]{"Semua Status", "Aktif", "Proses Klaim", "Dikembalikan"});
        cbKategori = new JComboBox<>(new String[]{"Semua Kategori", "Elektronik", "Dokumen", "Aksesoris", "Lainnya"});
        
        btnReset = new JButton("Reset Filter");
        btnReset.setBackground(Color.WHITE);
        btnReset.setFocusPainted(false);

        filterCard.add(tfSearch);
        filterCard.add(cbStatus);
        filterCard.add(cbKategori);
        filterCard.add(btnReset);

        centerPanel.add(filterCard, BorderLayout.NORTH);

        String[] columns = {"Barang", "Kategori", "Jenis Laporan", "Lokasi Kejadian", "Status Saat Ini", "Aksi"};
        tableModel = new DefaultTableModel(columns, 0);

        table = new JTable(tableModel);
        table.setRowHeight(60);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainLayout.add(centerPanel, BorderLayout.CENTER);
        add(mainLayout);

        if (bindDefaultController) {
            new AdminKelolaLaporanController(this).bind();
        }
    }

    public void renderTable(List<Item> laporanList) {
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

    public void applyFilter(String text) {
        if (text == null || text.trim().length() == 0) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    public void resetFilterFields() {
        tfSearch.setText("");
        cbStatus.setSelectedIndex(0);
        cbKategori.setSelectedIndex(0);
        rowSorter.setRowFilter(null);
    }

    public String getSearchText() { return tfSearch.getText(); }

    public void addSearchDocumentListener(DocumentListener l) { tfSearch.getDocument().addDocumentListener(l); }
    public void addResetListener(ActionListener l) { btnReset.addActionListener(l); }

    public void addTableActionClickListener(Consumer<Integer> onRowClicked) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (col == 5 && row >= 0) {
                    int modelRow = table.convertRowIndexToModel(row);
                    onRowClicked.accept(modelRow);
                }
            }
        });
    }

    public void setMenuNavigationHandler(Consumer<Integer> handler) {
        this.menuNavigationHandler = handler;
    }
}
