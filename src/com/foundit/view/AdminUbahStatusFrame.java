package com.foundit.view;

import com.foundit.model.Item;
import com.foundit.model.ItemStatus;
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

public class AdminUbahStatusFrame extends JFrame {

    private final ItemService itemService = new ItemService();
    private List<Item> currentItems = new ArrayList<>();

    private DefaultTableModel tableModel;
    private JTable table;

    public AdminUbahStatusFrame() {
        setTitle("Ubah Status Barang - FoundIt Admin");
        setSize(1150, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(new AdminSidebarPanel(this, AdminSidebarPanel.Menu.UBAH_STATUS), BorderLayout.WEST);

        JPanel mainLayout = new JPanel(new BorderLayout(0, 20));
        mainLayout.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainLayout.setBackground(new Color(247, 249, 252));

        mainLayout.add(AdminSidebarPanel.buildTopbar(
                "Ubah Status Barang",
                "Perbarui status barang hilang, ditemukan, atau selesai dikembalikan."
        ), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);

        JPanel infoCard = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoCard.setBackground(new Color(234, 245, 252));
        infoCard.setBorder(new EmptyBorder(12, 15, 12, 15));
        infoCard.setMaximumSize(new Dimension(2000, 50));
        infoCard.add(new JLabel("Klik Ubah Status pada baris barang untuk memperbarui status alur kerja di database."));
        centerPanel.add(infoCard, BorderLayout.NORTH);

        String[] columns = {"Barang", "Lokasi", "Status Saat Ini", "Aksi"};
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
                if (col == 3 && row >= 0 && row < currentItems.size()) {
                    handleUbahStatus(currentItems.get(row));
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
            currentItems = itemService.findAll();
            for (Item it : currentItems) {
                String desc = it.getDescription() != null ? it.getDescription() : "";
                String loc = it.getLocation() != null ? it.getLocation() : "-";
                String st = it.getStatus() != null ? it.getStatus().toString() : "-";

                tableModel.addRow(new Object[]{
                        "<html><b>" + it.getTitle() + "</b><br><font color='#777'>" + desc + "</font></html>",
                        loc,
                        "<html><b>" + st + "</b></html>",
                        "<html><font color='#3498db'><u>Ubah Status</u></font></html>"
                });
            }
        } catch (ServiceException ex) {
            UiKit.error(this, ex.getMessage());
        }
    }

    private void handleUbahStatus(Item item) {
        ItemStatus[] choices = {ItemStatus.TERBIT, ItemStatus.DITOLAK, ItemStatus.SELESAI, ItemStatus.ARSIP};
        ItemStatus selected = (ItemStatus) JOptionPane.showInputDialog(
                this,
                "Pilih status target untuk \"" + item.getTitle() + "\" (saat ini: " + item.getStatus() + "):",
                "Ubah Status Barang",
                JOptionPane.PLAIN_MESSAGE,
                null,
                choices,
                choices[0]
        );
        if (selected != null) {
            try {
                itemService.changeStatus(item.getId(), selected);
                refreshTable();
                UiKit.info(this, "Status barang berhasil diperbarui menjadi: " + selected);
            } catch (ServiceException ex) {
                UiKit.warn(this, ex.getMessage());
            }
        }
    }
}