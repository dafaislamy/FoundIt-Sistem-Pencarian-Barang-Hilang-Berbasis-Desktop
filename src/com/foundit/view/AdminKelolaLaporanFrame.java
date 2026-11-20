package com.foundit.view;

import com.foundit.model.Item;
import com.foundit.service.ItemService;
import com.foundit.service.ServiceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class AdminKelolaLaporanFrame extends JFrame {

    private final ItemService itemService = new ItemService();
    private List<Item> currentItems = new ArrayList<>();

    private JTextField tfSearch;
    private DefaultTableModel tableModel;
    private JTable table;

    public AdminKelolaLaporanFrame() {
        setTitle("Kelola Laporan Barang - FoundIt Admin");
        setSize(1150, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(new AdminSidebarPanel(this, AdminSidebarPanel.Menu.KELOLA_LAPORAN), BorderLayout.WEST);

        JPanel mainLayout = new JPanel(new BorderLayout(0, 20));
        mainLayout.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainLayout.setBackground(new Color(247, 249, 252));

        mainLayout.add(AdminSidebarPanel.buildTopbar(
                "Kelola Laporan Barang",
                "Pantau dan kelola seluruh laporan barang hilang dan ditemukan di kampus."
        ), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);

        JPanel controlCard = new JPanel(new BorderLayout(12, 0));
        controlCard.setBackground(Color.WHITE);
        controlCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                new EmptyBorder(15, 15, 15, 15)
        ));
        controlCard.setMaximumSize(new Dimension(2000, 75));

        tfSearch = new JTextField();
        tfSearch.setToolTipText("Cari nama barang, lokasi, atau kategori...");
        tfSearch.addActionListener(e -> refreshTable());

        JButton btnCari = new JButton("Cari");
        btnCari.addActionListener(e -> refreshTable());

        JButton btnRefresh = new JButton("Muat Ulang");
        btnRefresh.setBackground(new Color(52, 152, 219));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> {
            tfSearch.setText("");
            refreshTable();
        });

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightButtons.setOpaque(false);
        rightButtons.add(btnCari);
        rightButtons.add(btnRefresh);

        controlCard.add(tfSearch, BorderLayout.CENTER);
        controlCard.add(rightButtons, BorderLayout.EAST);
        centerPanel.add(controlCard, BorderLayout.NORTH);

        String[] columns = {"Barang", "Kategori", "Lokasi", "Status", "Aksi"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(60);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (col == 4 && row >= 0 && row < currentItems.size()) {
                    handleRowAction(currentItems.get(row));
                }
            }
        });

        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainLayout.add(centerPanel, BorderLayout.CENTER);
        add(mainLayout, BorderLayout.CENTER);

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        try {
            currentItems = itemService.search(tfSearch.getText(), null, null, null);
            for (Item it : currentItems) {
                String desc = it.getDescription() != null ? it.getDescription() : "";
                String kat = it.getCategoryName() != null ? it.getCategoryName() : "Umum";
                String loc = it.getLocation() != null ? it.getLocation() : "-";
                String st = it.getStatus() != null ? it.getStatus().toString() : "-";

                tableModel.addRow(new Object[]{
                        "<html><b>" + it.getTitle() + "</b><br><font color='#777'>" + desc + "</font></html>",
                        kat,
                        loc,
                        st,
                        "<html><font color='#dc3545'><u>Hapus</u></font></html>"
                });
            }
        } catch (ServiceException ex) {
            UiKit.error(this, ex.getMessage());
        }
    }

    private void handleRowAction(Item item) {
        if (UiKit.confirm(this, "Hapus laporan barang \"" + item.getTitle() + "\" dari database?")) {
            try {
                itemService.delete(item.getId());
                refreshTable();
                UiKit.info(this, "Laporan barang berhasil dihapus!");
            } catch (ServiceException ex) {
                UiKit.error(this, ex.getMessage());
            }
        }
    }
}