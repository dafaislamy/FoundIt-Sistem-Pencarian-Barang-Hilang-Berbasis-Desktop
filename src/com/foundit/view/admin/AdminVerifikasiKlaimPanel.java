package com.foundit.view.admin;

import com.foundit.controller.AdminController;
import com.foundit.model.Account;
import com.foundit.model.Claim;
import com.foundit.model.ClaimStatus;
import com.foundit.service.ServiceException;
import com.foundit.util.ImageHelper;
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

/**
 * AdminVerifikasiKlaimPanel — Menampilkan daftar klaim user beserta bukti kepemilikan
 * dan lampiran foto, serta tombol Setujui / Tolak klaim ke MySQL.
 */
public class AdminVerifikasiKlaimPanel extends JPanel {

    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final AdminController controller;
    private JComboBox<String> statusFilter;
    private DefaultTableModel tableModel;
    private JTable table;
    private List<Claim> claimList = new ArrayList<>();

    public AdminVerifikasiKlaimPanel(AdminController controller) {
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

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        JLabel title = new JLabel("Daftar Klaim Barang");
        title.setFont(ThemeConfig.FONT_TITLE);
        title.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);

        statusFilter = new JComboBox<>(new String[]{"Semua Status", "Menunggu", "Disetujui", "Ditolak", "Dibatalkan"});
        statusFilter.setBackground(Color.WHITE);
        statusFilter.setFont(ThemeConfig.FONT_MEDIUM);
        statusFilter.setPreferredSize(new Dimension(160, 36));
        statusFilter.addActionListener(e -> loadClaims());

        left.add(title);
        left.add(statusFilter);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        RoundedButton detailBtn = RoundedButton.createOutlineButton("Lihat Bukti");
        detailBtn.setPreferredSize(new Dimension(115, 36));
        detailBtn.addActionListener(e -> showProofDetail());

        RoundedButton approveBtn = new RoundedButton("Setujui Klaim");
        approveBtn.setPreferredSize(new Dimension(130, 36));
        approveBtn.addActionListener(e -> processClaim(true));

        RoundedButton rejectBtn = RoundedButton.createDangerButton("Tolak Klaim");
        rejectBtn.setPreferredSize(new Dimension(120, 36));
        rejectBtn.addActionListener(e -> processClaim(false));

        actions.add(detailBtn);
        actions.add(approveBtn);
        actions.add(rejectBtn);

        topBar.add(left, BorderLayout.WEST);
        topBar.add(actions, BorderLayout.EAST);
        card.add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID Klaim", "Barang", "Pemohon", "Bukti Kepemilikan", "Tanggal", "Status"};
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

        table.getColumnModel().getColumn(5).setCellRenderer(new ClaimStatusRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeConfig.BORDER_CARD));
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    public void refreshData() {
        loadClaims();
    }

    private void loadClaims() {
        ClaimStatus filter = switch (statusFilter.getSelectedIndex()) {
            case 1 -> ClaimStatus.MENUNGGU;
            case 2 -> ClaimStatus.DISETUJUI;
            case 3 -> ClaimStatus.DITOLAK;
            case 4 -> ClaimStatus.DIBATALKAN;
            default -> null;
        };

        tableModel.setRowCount(0);
        try {
            claimList = controller.searchClaims(null, filter);
            for (Claim c : claimList) {
                String tgl = c.getCreatedAt() != null ? c.getCreatedAt().format(TS_FMT) : "-";
                tableModel.addRow(new Object[]{
                        c.getId(),
                        c.getItemTitle() != null ? c.getItemTitle() : c.getItemId(),
                        c.getClaimantName() != null ? c.getClaimantName() : c.getUserId(),
                        c.getProofDescription(),
                        tgl,
                        c.getStatus().getDb()
                });
            }
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(ThemeConfig.windowOf(this), "Gagal memuat data klaim: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Claim getSelected() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= claimList.size()) {
            JOptionPane.showMessageDialog(ThemeConfig.windowOf(this), "Pilih baris klaim terlebih dahulu.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return claimList.get(row);
    }

    private void showProofDetail() {
        Claim c = getSelected();
        if (c == null) return;

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Bukti Klaim - " + c.getId(), Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(480, 500);
        dlg.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel img = new JLabel(ImageHelper.loadScaledIcon(c.getAttachment(), 420, 190));
        img.setHorizontalAlignment(SwingConstants.CENTER);
        root.add(img, BorderLayout.NORTH);

        JTextArea text = new JTextArea(
                "ID Klaim: " + c.getId()
                + "\nBarang: " + (c.getItemTitle() != null ? c.getItemTitle() : c.getItemId())
                + "\nPemohon: " + (c.getClaimantName() != null ? c.getClaimantName() : c.getUserId())
                + "\nStatus: " + c.getStatus().getDb()
                + "\n\nBukti Kepemilikan:\n" + c.getProofDescription()
        );
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setFont(ThemeConfig.FONT_MEDIUM);
        text.setBackground(ThemeConfig.BG_PAGE);
        text.setBorder(new EmptyBorder(10, 12, 10, 12));
        root.add(new JScrollPane(text), BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        footer.setOpaque(false);
        RoundedButton close = RoundedButton.createOutlineButton("Tutup");
        close.setPreferredSize(new Dimension(100, 36));
        close.addActionListener(e -> dlg.dispose());
        footer.add(close);
        root.add(footer, BorderLayout.SOUTH);

        dlg.setContentPane(root);
        dlg.setVisible(true);
    }

    private void processClaim(boolean approve) {
        Claim c = getSelected();
        if (c == null) return;

        if (c.getStatus() != ClaimStatus.MENUNGGU) {
            JOptionPane.showMessageDialog(ThemeConfig.windowOf(this), "Klaim ini sudah diproses (" + c.getStatus().getDb() + ").",
                    "Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Account admin = Session.getCurrent();
        String adminId = admin != null ? admin.getId() : "ADM001";

        String msg = approve
                ? "Setujui klaim dari " + c.getClaimantName() + "?\nStatus barang akan otomatis menjadi 'Selesai' dan klaim lain pada barang ini akan ditolak."
                : "Tolak klaim dari " + c.getClaimantName() + "?";

        if (JOptionPane.showConfirmDialog(ThemeConfig.windowOf(this), msg, "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                if (approve) controller.approveClaim(c.getId(), adminId);
                else controller.rejectClaim(c.getId(), adminId);

                JOptionPane.showMessageDialog(ThemeConfig.windowOf(this),
                        approve ? "Klaim disetujui! Barang ditandai Selesai." : "Klaim ditolak.",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadClaims();
            } catch (ServiceException ex) {
                JOptionPane.showMessageDialog(ThemeConfig.windowOf(this), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class ClaimStatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
            p.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            p.add(StatusBadge.forKlaimStatus(value != null ? value.toString() : "Menunggu"));
            return p;
        }
    }
}
