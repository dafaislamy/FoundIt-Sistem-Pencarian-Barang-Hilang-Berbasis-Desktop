package com.foundit.view.user;

import com.foundit.controller.UserController;
import com.foundit.model.Account;
import com.foundit.model.Claim;
import com.foundit.model.ClaimStatus;
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
 * StatusKlaimPanel — Menampilkan daftar klaim barang milik User yang sedang login
 * beserta status (Menunggu, Disetujui, Ditolak, Dibatalkan) dan aksi batalkan klaim.
 */
public class StatusKlaimPanel extends JPanel {

    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final UserController controller;
    private final Consumer<String> navigator;

    private DefaultTableModel tableModel;
    private JTable table;
    private List<Claim> currentClaims = new ArrayList<>();
    private RoundedButton cancelBtn;
    private RoundedButton detailBtn;

    public StatusKlaimPanel(UserController controller, Consumer<String> navigator) {
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

        // Top action bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel title = new JLabel("Daftar Pengajuan Klaim Saya");
        title.setFont(ThemeConfig.FONT_TITLE);
        title.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnGroup.setOpaque(false);

        RoundedButton newClaimBtn = new RoundedButton("+ Ajukan Klaim Baru");
        newClaimBtn.setPreferredSize(new Dimension(165, 36));
        newClaimBtn.addActionListener(e -> navigator.accept("klaim"));

        detailBtn = RoundedButton.createOutlineButton("Detail Klaim");
        detailBtn.setPreferredSize(new Dimension(120, 36));
        detailBtn.addActionListener(e -> showSelectedDetail());

        cancelBtn = RoundedButton.createDangerButton("Batalkan Klaim");
        cancelBtn.setPreferredSize(new Dimension(140, 36));
        cancelBtn.addActionListener(e -> cancelSelectedClaim());

        btnGroup.add(detailBtn);
        btnGroup.add(cancelBtn);
        btnGroup.add(newClaimBtn);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(btnGroup, BorderLayout.EAST);
        card.add(topBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID Klaim", "Barang", "Bukti Kepemilikan", "Tanggal Ajuan", "Status"};
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

        // Renderer khusus kolom Status (badge)
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());
        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(190);
        table.getColumnModel().getColumn(2).setPreferredWidth(300);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);

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
            currentClaims = controller.getUserClaims(acc.getId());
            for (Claim c : currentClaims) {
                String tgl = c.getCreatedAt() != null ? c.getCreatedAt().format(TS_FMT) : "-";
                tableModel.addRow(new Object[]{
                        c.getId(),
                        c.getItemTitle() != null ? c.getItemTitle() : c.getItemId(),
                        c.getProofDescription(),
                        tgl,
                        c.getStatus().getDb()
                });
            }
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat status klaim: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSelectedDetail() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentClaims.size()) {
            JOptionPane.showMessageDialog(this, "Pilih baris klaim terlebih dahulu.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Claim c = currentClaims.get(row);
        String msg = String.format(
                "ID Klaim: %s\nBarang: %s\nStatus: %s\nBukti Kepemilikan:\n%s\n\nAdmin Pemroses: %s",
                c.getId(),
                c.getItemTitle() != null ? c.getItemTitle() : c.getItemId(),
                c.getStatus().getDb(),
                c.getProofDescription(),
                c.getAdminName() != null ? c.getAdminName() : "Belum diproses"
        );
        JOptionPane.showMessageDialog(this, msg, "Detail Klaim " + c.getId(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelSelectedClaim() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentClaims.size()) {
            JOptionPane.showMessageDialog(this, "Pilih klaim yang ingin dibatalkan.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Claim c = currentClaims.get(row);
        if (c.getStatus() != ClaimStatus.MENUNGGU) {
            JOptionPane.showMessageDialog(this, "Hanya klaim berstatus Menunggu yang dapat dibatalkan.",
                    "Tidak Dapat Dibatalkan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin membatalkan klaim untuk \"" + c.getItemTitle() + "\"?",
                "Konfirmasi Batalkan Klaim", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.cancelClaim(c.getId());
                JOptionPane.showMessageDialog(this, "Klaim berhasil dibatalkan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (ServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JPanel cell = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
            cell.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            String statusStr = value != null ? value.toString() : "Menunggu";
            cell.add(StatusBadge.forKlaimStatus(statusStr));
            return cell;
        }
    }
}
