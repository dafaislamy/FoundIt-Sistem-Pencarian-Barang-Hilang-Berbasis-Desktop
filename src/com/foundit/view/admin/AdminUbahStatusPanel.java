package com.foundit.view.admin;

import com.foundit.controller.AdminController;
import com.foundit.model.Item;
import com.foundit.model.ItemStatus;
import com.foundit.service.ServiceException;
import com.foundit.view.ThemeConfig;
import com.foundit.view.components.RoundedButton;
import com.foundit.view.components.RoundedPanel;
import com.foundit.view.components.RoundedTextField;
import com.foundit.view.components.StatusBadge;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminUbahStatusPanel — Manajemen perubahan status barang (Terbit, Selesai, Ditolak, Arsip)
 * sesuai alur bisnis ItemService & admin-ubah-status.php.
 */
public class AdminUbahStatusPanel extends JPanel {

    private final AdminController controller;
    private RoundedTextField searchField;
    private JComboBox<String> statusFilter;
    private DefaultTableModel tableModel;
    private JTable table;
    private List<Item> itemList = new ArrayList<>();

    public AdminUbahStatusPanel(AdminController controller) {
        this.controller = controller;
        setOpaque(false);
        setLayout(new BorderLayout());
        add(buildCard(), BorderLayout.CENTER);
    }

    private RoundedPanel buildCard() {
        RoundedPanel card = new RoundedPanel(ThemeConfig.CARD_RADIUS, ThemeConfig.BORDER_CARD, 1, false);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(new EmptyBorder(22, 24, 22, 24));

        JPanel topBar = new JPanel(new BorderLayout(12, 0));
        topBar.setOpaque(false);

        JPanel filterGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterGroup.setOpaque(false);

        searchField = new RoundedTextField(8);
        searchField.setPreferredSize(new Dimension(210, 36));
        searchField.addActionListener(e -> loadItems());

        statusFilter = new JComboBox<>(new String[]{"Semua Status", "Pending", "Terbit", "Selesai", "Ditolak", "Arsip"});
        statusFilter.setBackground(Color.WHITE);
        statusFilter.setFont(ThemeConfig.FONT_MEDIUM);
        statusFilter.setPreferredSize(new Dimension(140, 36));
        statusFilter.addActionListener(e -> loadItems());

        RoundedButton cariBtn = new RoundedButton("Cari");
        cariBtn.setPreferredSize(new Dimension(75, 36));
        cariBtn.addActionListener(e -> loadItems());

        filterGroup.add(searchField);
        filterGroup.add(statusFilter);
        filterGroup.add(cariBtn);

        JPanel actionGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionGroup.setOpaque(false);

        RoundedButton btnTerbit = new RoundedButton("Terbitkan");
        btnTerbit.setPreferredSize(new Dimension(105, 36));
        btnTerbit.addActionListener(e -> changeSelectedStatus(ItemStatus.TERBIT));

        RoundedButton btnSelesai = RoundedButton.createOutlineButton("Tandai Selesai");
        btnSelesai.setPreferredSize(new Dimension(130, 36));
        btnSelesai.addActionListener(e -> changeSelectedStatus(ItemStatus.SELESAI));

        RoundedButton btnArsip = new RoundedButton("Arsipkan", RoundedButton.Variant.RESET, 8);
        btnArsip.setPreferredSize(new Dimension(95, 36));
        btnArsip.addActionListener(e -> changeSelectedStatus(ItemStatus.ARSIP));

        RoundedButton btnTolak = RoundedButton.createDangerButton("Tolak");
        btnTolak.setPreferredSize(new Dimension(80, 36));
        btnTolak.addActionListener(e -> changeSelectedStatus(ItemStatus.DITOLAK));

        actionGroup.add(btnTerbit);
        actionGroup.add(btnSelesai);
        actionGroup.add(btnArsip);
        actionGroup.add(btnTolak);

        topBar.add(filterGroup, BorderLayout.WEST);
        topBar.add(actionGroup, BorderLayout.EAST);
        card.add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Judul Barang", "Tipe", "Kategori", "Lokasi", "Status Saat Ini"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(46);
        table.setFont(ThemeConfig.FONT_MEDIUM);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowVerticalLines(false);
        table.setGridColor(ThemeConfig.TABLE_ROW_BORDER);
        table.getTableHeader().setFont(ThemeConfig.FONT_SEMIBOLD);
        table.getTableHeader().setBackground(ThemeConfig.TABLE_HEADER_BG);
        table.getTableHeader().setForeground(ThemeConfig.TABLE_HEADER_FG);
        table.getTableHeader().setPreferredSize(new Dimension(0, 42));

        table.getColumnModel().getColumn(2).setCellRenderer(new TypeRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeConfig.BORDER_CARD));
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    public void refreshData() {
        loadItems();
    }

    private void loadItems() {
        ItemStatus status = switch (statusFilter.getSelectedIndex()) {
            case 1 -> ItemStatus.PENDING;
            case 2 -> ItemStatus.TERBIT;
            case 3 -> ItemStatus.SELESAI;
            case 4 -> ItemStatus.DITOLAK;
            case 5 -> ItemStatus.ARSIP;
            default -> null;
        };

        tableModel.setRowCount(0);
        try {
            itemList = controller.searchItems(searchField.getText(), null, status, null);
            for (Item it : itemList) {
                tableModel.addRow(new Object[]{
                        it.getId(),
                        it.getTitle(),
                        it.getType().getDb(),
                        it.getCategoryName() != null ? it.getCategoryName() : "-",
                        it.getLocation(),
                        it.getStatus().getDb()
                });
            }
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(ThemeConfig.windowOf(this), "Gagal memuat data barang: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changeSelectedStatus(ItemStatus target) {
        int row = table.getSelectedRow();
        if (row < 0 || row >= itemList.size()) {
            JOptionPane.showMessageDialog(ThemeConfig.windowOf(this), "Pilih barang terlebih dahulu.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Item it = itemList.get(row);
        try {
            controller.changeItemStatus(it.getId(), target);
            JOptionPane.showMessageDialog(ThemeConfig.windowOf(this),
                    "Status barang \"" + it.getTitle() + "\" berhasil diubah menjadi " + target.getDb() + ".",
                    "Status Diperbarui", JOptionPane.INFORMATION_MESSAGE);
            loadItems();
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(ThemeConfig.windowOf(this), ex.getMessage(), "Perubahan Status Ditolak", JOptionPane.WARNING_MESSAGE);
        }
    }

    private static class TypeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
            p.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            p.add(StatusBadge.forType(value != null ? value.toString() : "lost"));
            return p;
        }
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
            p.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            p.add(StatusBadge.forItemStatus(value != null ? value.toString() : "Pending"));
            return p;
        }
    }
}
