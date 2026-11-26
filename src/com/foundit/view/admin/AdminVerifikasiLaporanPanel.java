package com.foundit.view.admin;

import com.foundit.controller.AdminController;
import com.foundit.model.Item;
import com.foundit.service.ServiceException;
import com.foundit.util.ImageHelper;
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

/**
 * AdminVerifikasiLaporanPanel — Daftar laporan berstatus Pending
 * dengan tombol Detail, Terima & Terbitkan, dan Tolak Laporan ke MySQL.
 */
public class AdminVerifikasiLaporanPanel extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final AdminController controller;
    private DefaultTableModel tableModel;
    private JTable table;
    private List<Item> pendingList = new ArrayList<>();

    public AdminVerifikasiLaporanPanel(AdminController controller) {
        this.controller = controller;
        setOpaque(false);
        setLayout(new BorderLayout());
        add(buildCard(), BorderLayout.CENTER);
    }

    private RoundedPanel buildCard() {
        RoundedPanel card = new RoundedPanel(ThemeConfig.CARD_RADIUS, ThemeConfig.BORDER_CARD, 1, false);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(new EmptyBorder(22, 24, 22, 24));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel title = new JLabel("Laporan Menunggu Verifikasi (Pending)");
        title.setFont(ThemeConfig.FONT_TITLE);
        title.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        RoundedButton detailBtn = RoundedButton.createOutlineButton("Detail & Foto");
        detailBtn.setPreferredSize(new Dimension(125, 36));
        detailBtn.addActionListener(e -> showDetail());

        RoundedButton approveBtn = new RoundedButton("Terima & Terbitkan");
        approveBtn.setPreferredSize(new Dimension(160, 36));
        approveBtn.addActionListener(e -> processSelected(true));

        RoundedButton rejectBtn = RoundedButton.createDangerButton("Tolak Laporan");
        rejectBtn.setPreferredSize(new Dimension(135, 36));
        rejectBtn.addActionListener(e -> processSelected(false));

        actions.add(detailBtn);
        actions.add(approveBtn);
        actions.add(rejectBtn);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(actions, BorderLayout.EAST);
        card.add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Judul Barang", "Tipe", "Kategori", "Lokasi", "Pelapor / Penemu", "Tanggal", "Status"};
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
        tableModel.setRowCount(0);
        try {
            pendingList = controller.getPendingItems(0);
            for (Item it : pendingList) {
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
            JOptionPane.showMessageDialog(this, "Gagal memuat laporan pending: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Item getSelected() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= pendingList.size()) {
            JOptionPane.showMessageDialog(this, "Pilih laporan terlebih dahulu.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return pendingList.get(row);
    }

    private void showDetail() {
        Item it = getSelected();
        if (it == null) return;

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Tinjau Laporan - " + it.getId(), Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(480, 520);
        dlg.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel img = new JLabel(ImageHelper.loadScaledIcon(it.getPhoto(), 420, 200));
        img.setHorizontalAlignment(SwingConstants.CENTER);
        root.add(img, BorderLayout.NORTH);

        JTextArea info = new JTextArea(
                "Judul: " + it.getTitle()
                + "\nTipe: " + it.getType()
                + "\nKategori: " + (it.getCategoryName() != null ? it.getCategoryName() : "-")
                + "\nLokasi: " + it.getLocation()
                + "\nTanggal: " + (it.getEventDate() != null ? it.getEventDate().format(DATE_FMT) : "-")
                + "\n\nDeskripsi:\n" + it.getDescription()
        );
        info.setEditable(false);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setFont(ThemeConfig.FONT_MEDIUM);
        info.setBackground(ThemeConfig.BG_PAGE);
        info.setBorder(new EmptyBorder(10, 12, 10, 12));
        root.add(new JScrollPane(info), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        RoundedButton approve = new RoundedButton("Terima & Terbitkan");
        approve.setPreferredSize(new Dimension(155, 36));
        approve.addActionListener(e -> { dlg.dispose(); verify(it, true); });
        RoundedButton reject = RoundedButton.createDangerButton("Tolak");
        reject.setPreferredSize(new Dimension(95, 36));
        reject.addActionListener(e -> { dlg.dispose(); verify(it, false); });
        btns.add(reject);
        btns.add(approve);
        root.add(btns, BorderLayout.SOUTH);

        dlg.setContentPane(root);
        dlg.setVisible(true);
    }

    private void processSelected(boolean approve) {
        Item it = getSelected();
        if (it == null) return;
        verify(it, approve);
    }

    private void verify(Item it, boolean approve) {
        String actionText = approve ? "menerbitkan" : "menolak";
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin " + actionText + " laporan \"" + it.getTitle() + "\"?",
                "Konfirmasi Verifikasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.verifyItem(it.getId(), approve);
                JOptionPane.showMessageDialog(this,
                        approve ? "Laporan berhasil diterbitkan!" : "Laporan berhasil ditolak.",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (ServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
