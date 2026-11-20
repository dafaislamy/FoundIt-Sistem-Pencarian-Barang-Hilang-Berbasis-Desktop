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

public class AdminVerifikasiLaporanFrame extends JFrame {

    private final ItemService itemService = new ItemService();
    private List<Item> currentItems = new ArrayList<>();

    private DefaultTableModel tableModel;
    private JTable table;

    public AdminVerifikasiLaporanFrame() {
        setTitle("Verifikasi Laporan Barang - FoundIt Admin");
        setSize(1150, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(new AdminSidebarPanel(this, AdminSidebarPanel.Menu.VERIFIKASI_LAPORAN), BorderLayout.WEST);

        JPanel mainLayout = new JPanel(new BorderLayout(0, 20));
        mainLayout.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainLayout.setBackground(new Color(247, 249, 252));

        mainLayout.add(AdminSidebarPanel.buildTopbar(
                "Verifikasi Laporan Barang",
                "Periksa keabsahan laporan yang masuk sebelum dipublikasikan."
        ), BorderLayout.NORTH);

        String[] columns = {"Barang & Deskripsi", "Lokasi", "Status", "Aksi"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(65);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (col == 3 && row >= 0 && row < currentItems.size()) {
                    handleVerifikasi(currentItems.get(row));
                }
            }
        });

        mainLayout.add(new JScrollPane(table), BorderLayout.CENTER);
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
                        st,
                        "<html><font color='#3498db'><u>Verifikasi</u></font></html>"
                });
            }
        } catch (ServiceException ex) {
            UiKit.error(this, ex.getMessage());
        }
    }

    private void handleVerifikasi(Item item) {
        String[] options = {"Terbitkan (Terbit)", "Tolak (Ditolak)", "Batal"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Verifikasi laporan \"" + item.getTitle() + "\"?\nLokasi: " + (item.getLocation() != null ? item.getLocation() : "-"),
                "Verifikasi Laporan",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        try {
            if (choice == 0) {
                itemService.changeStatus(item.getId(), ItemStatus.TERBIT);
                refreshTable();
                UiKit.info(this, "Laporan berhasil diterbitkan!");
            } else if (choice == 1) {
                itemService.changeStatus(item.getId(), ItemStatus.DITOLAK);
                refreshTable();
                UiKit.info(this, "Laporan ditolak.");
            }
        } catch (ServiceException ex) {
            UiKit.warn(this, ex.getMessage());
        }
    }
}