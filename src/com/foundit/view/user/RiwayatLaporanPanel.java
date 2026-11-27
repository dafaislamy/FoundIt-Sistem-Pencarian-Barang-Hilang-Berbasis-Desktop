package com.foundit.view.user;

import com.foundit.controller.UserController;
import com.foundit.model.Account;
import com.foundit.model.Item;
import com.foundit.model.ItemStatus;
import com.foundit.service.ServiceException;
import com.foundit.util.Session;
import com.foundit.view.ThemeConfig;
import com.foundit.view.components.RoundedButton;
import com.foundit.view.components.RoundedPanel;
import com.foundit.view.components.StatusBadge;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * RiwayatLaporanPanel — Menampilkan seluruh laporan barang hilang & ditemukan
 * yang dibuat oleh User yang sedang login, lengkap dengan status badge & aksi hapus (Pending).
 */
public class RiwayatLaporanPanel extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final UserController controller;
    private final Consumer<String> navigator;

    private DefaultTableModel tableModel;
    private JTable table;
    private List<Item> myReports = new ArrayList<>();

    public RiwayatLaporanPanel(UserController controller, Consumer<String> navigator) {
        this.controller = controller;
        this.navigator = navigator;

        setOpaque(false);
        setLayout(new BorderLayout(0, 16));
        add(buildCard(), BorderLayout.CENTER);
    }

    private RoundedPanel buildCard() {
        RoundedPanel card = new RoundedPanel(ThemeConfig.CARD_RADIUS, ThemeConfig.BORDER_CARD, 1, false);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(new EmptyBorder(22, 24, 22, 24));

        // Top Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel title = new JLabel("Riwayat Laporan Saya");
        title.setFont(ThemeConfig.FONT_TITLE);
        title.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnGroup.setOpaque(false);

        RoundedButton detailBtn = RoundedButton.createOutlineButton("Detail");
        detailBtn.setPreferredSize(new Dimension(95, 36));
        detailBtn.addActionListener(e -> showSelectedDetail());

        RoundedButton deleteBtn = RoundedButton.createDangerButton("Hapus (Pending)");
        deleteBtn.setPreferredSize(new Dimension(145, 36));
        deleteBtn.addActionListener(e -> deleteSelectedReport());

        RoundedButton addLostBtn = new RoundedButton("+ Lapor Hilang");
        addLostBtn.setPreferredSize(new Dimension(135, 36));
        addLostBtn.addActionListener(e -> navigator.accept("lapor-hilang"));

        btnGroup.add(detailBtn);
        btnGroup.add(deleteBtn);
        btnGroup.add(addLostBtn);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(btnGroup, BorderLayout.EAST);
        card.add(topBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Judul Barang", "Tipe", "Kategori", "Lokasi", "Tanggal", "Status"};
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

        table.getColumnModel().getColumn(2).setCellRenderer(new TypeBadgeRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new ItemStatusBadgeRenderer());

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(160);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);
        table.getColumnModel().getColumn(6).setPreferredWidth(110);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeConfig.BORDER_CARD));
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    public void refreshData() {
        Account acc = Session.getCurrent();
        if (acc == null) return;

        tableModel.setRowCount(0);
        try {
            myReports = controller.getUserReports(acc.getId());
            for (Item it : myReports) {
                String tgl = it.getEventDate() != null ? it.getEventDate().format(DATE_FMT) : "-";
                tableModel.addRow(new Object[]{
                        it.getId(),
                        it.getTitle(),
                        it.getType().getDb(),
                        it.getCategoryName() != null ? it.getCategoryName() : "-",
                        it.getLocation(),
                        tgl,
                        it.getStatus().getDb()
                });
            }
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(ThemeConfig.windowOf(this), "Gagal memuat riwayat laporan: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSelectedDetail() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= myReports.size()) {
            JOptionPane.showMessageDialog(ThemeConfig.windowOf(this), "Pilih laporan terlebih dahulu.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Item it = myReports.get(row);
        String msg = String.format(
                "ID: %s\nJudul: %s\nTipe: %s\nKategori: %s\nLokasi: %s\nTanggal: %s\nStatus: %s\n\nDeskripsi:\n%s",
                it.getId(),
                it.getTitle(),
                it.getType(),
                it.getCategoryName() != null ? it.getCategoryName() : "-",
                it.getLocation(),
                it.getEventDate() != null ? it.getEventDate().format(DATE_FMT) : "-",
                it.getStatus().getDb(),
                it.getDescription()
        );
        JOptionPane.showMessageDialog(ThemeConfig.windowOf(this), msg, "Detail Laporan " + it.getId(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteSelectedReport() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= myReports.size()) {
            JOptionPane.showMessageDialog(ThemeConfig.windowOf(this), "Pilih laporan yang ingin dihapus.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Item it = myReports.get(row);
        if (it.getStatus() != ItemStatus.PENDING) {
            JOptionPane.showMessageDialog(ThemeConfig.windowOf(this), "Hanya laporan berstatus Pending yang dapat dihapus.",
                    "Tidak Dapat Dihapus", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(ThemeConfig.windowOf(this),
                "Yakin ingin menghapus laporan \"" + it.getTitle() + "\"?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.deleteUserReport(it.getId());
                JOptionPane.showMessageDialog(ThemeConfig.windowOf(this), "Laporan berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (ServiceException ex) {
                JOptionPane.showMessageDialog(ThemeConfig.windowOf(this), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class TypeBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JPanel cell = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
            cell.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            cell.add(StatusBadge.forType(value != null ? value.toString() : "lost"));
            return cell;
        }
    }

    private static class ItemStatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JPanel cell = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
            cell.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            cell.add(StatusBadge.forItemStatus(value != null ? value.toString() : "Pending"));
            return cell;
        }
    }
}
