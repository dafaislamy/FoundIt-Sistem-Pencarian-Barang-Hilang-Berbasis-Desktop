package com.foundit.view.admin;

import com.foundit.controller.AdminController;
import com.foundit.model.Item;
import com.foundit.model.ItemType;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminKelolaLaporanPanel — Daftar seluruh laporan barang dengan pencarian,
 * filter tipe, detail laporan, dan hapus laporan.
 */
public class AdminKelolaLaporanPanel extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final AdminController controller;
    private RoundedTextField searchField;
    private JComboBox<String> tipeFilter;
    private DefaultTableModel tableModel;
    private JTable table;
    private List<Item> items = new ArrayList<>();

    public AdminKelolaLaporanPanel(AdminController controller) {
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

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setOpaque(false);

        searchField = new RoundedTextField(8);
        searchField.setPreferredSize(new Dimension(230, 36));
        searchField.addActionListener(e -> loadItems());

        tipeFilter = new JComboBox<>(new String[]{"Semua Tipe", "Hilang", "Ditemukan"});
        tipeFilter.setBackground(Color.WHITE);
        tipeFilter.setFont(ThemeConfig.FONT_MEDIUM);
        tipeFilter.setPreferredSize(new Dimension(140, 36));
        tipeFilter.addActionListener(e -> loadItems());

        RoundedButton cariBtn = new RoundedButton("Cari");
        cariBtn.setPreferredSize(new Dimension(80, 36));
        cariBtn.addActionListener(e -> loadItems());

        filters.add(searchField);
        filters.add(tipeFilter);
        filters.add(cariBtn);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        RoundedButton detailBtn = RoundedButton.createOutlineButton("Detail");
        detailBtn.setPreferredSize(new Dimension(95, 36));
        detailBtn.addActionListener(e -> showDetail());

        RoundedButton deleteBtn = RoundedButton.createDangerButton("Hapus Laporan");
        deleteBtn.setPreferredSize(new Dimension(135, 36));
        deleteBtn.addActionListener(e -> deleteSelected());

        actions.add(detailBtn);
        actions.add(deleteBtn);

        topBar.add(filters, BorderLayout.WEST);
        topBar.add(actions, BorderLayout.EAST);
        card.add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Judul Barang", "Tipe", "Kategori", "Lokasi", "Pelapor/Penemu", "Tanggal", "Status"};
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
        table.getColumnModel().getColumn(7).setCellRenderer(new StatusRenderer());

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
        ItemType type = switch (tipeFilter.getSelectedIndex()) {
            case 1 -> ItemType.LOST;
            case 2 -> ItemType.FOUND;
            default -> null;
        };

        tableModel.setRowCount(0);
        try {
            items = controller.searchItems(searchField.getText(), null, null, type);
            for (Item it : items) {
                String person = it.getReporterName() != null
                        ? it.getReporterName()
                        : (it.getFinderName() != null ? it.getFinderName() : "-");
                String tgl = it.getEventDate() != null ? it.getEventDate().format(DATE_FMT) : "-";
                tableModel.addRow(new Object[]{
                        it.getId(),
                        it.getTitle(),
                        it.getType().getDb(),
                        it.getCategoryName() != null ? it.getCategoryName() : "-",
                        it.getLocation(),
                        person,
                        tgl,
                        it.getStatus().getDb()
                });
            }
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat laporan: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDetail() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= items.size()) {
            JOptionPane.showMessageDialog(this, "Pilih baris laporan terlebih dahulu.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Item it = items.get(row);
        String msg = String.format(
                "ID: %s\nJudul: %s\nTipe: %s\nKategori: %s\nLokasi: %s\nStatus: %s\n\nDeskripsi:\n%s",
                it.getId(), it.getTitle(), it.getType(), it.getCategoryName(),
                it.getLocation(), it.getStatus().getDb(), it.getDescription()
        );
        JOptionPane.showMessageDialog(this, msg, "Detail Laporan " + it.getId(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= items.size()) {
            JOptionPane.showMessageDialog(this, "Pilih laporan yang ingin dihapus.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Item it = items.get(row);
        if (JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus laporan \"" + it.getTitle() + "\"?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                controller.deleteItem(it.getId());
                JOptionPane.showMessageDialog(this, "Laporan berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadItems();
            } catch (ServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal Menghapus", JOptionPane.WARNING_MESSAGE);
            }
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
