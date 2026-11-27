package com.foundit.view;

import com.foundit.model.Claim;
import com.foundit.service.ClaimService;
import com.foundit.service.ServiceException;
import com.foundit.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class AdminVerifikasiKlaimFrame extends JFrame {

    private final ClaimService claimService = new ClaimService();
    private List<Claim> currentClaims = new ArrayList<>();

    private DefaultTableModel tableModel;
    private JTable table;

    public AdminVerifikasiKlaimFrame() {
        setTitle("Verifikasi Klaim Barang - FoundIt Admin");
        setSize(1150, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        ThemeConfig.applyMaximized(this);
        setLayout(new BorderLayout());

        add(new AdminSidebarPanel(this, AdminSidebarPanel.Menu.VERIFIKASI_KLAIM), BorderLayout.WEST);

        JPanel mainLayout = new JPanel(new BorderLayout(0, 20));
        mainLayout.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainLayout.setBackground(new Color(247, 249, 252));

        mainLayout.add(AdminSidebarPanel.buildTopbar(
                "Verifikasi Klaim Barang",
                "Tinjau bukti kepemilikan dan setujui atau tolak pengajuan klaim pengguna."
        ), BorderLayout.NORTH);

        String[] columns = {"Barang & Bukti Klaim", "Pengklaim", "Status", "Aksi"};
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
                if (col == 3 && row >= 0 && row < currentClaims.size()) {
                    handleVerifikasi(currentClaims.get(row));
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
            currentClaims = claimService.findAll();
            for (Claim c : currentClaims) {
                String itemTitle = c.getItemTitle() != null ? c.getItemTitle() : ("Barang #" + c.getItemId());
                String proof = c.getProofDescription() != null ? c.getProofDescription() : "-";
                String claimant = c.getClaimantName() != null ? c.getClaimantName() : ("User #" + c.getUserId());
                String statusText = c.getStatus() != null ? c.getStatus().toString() : "-";

                tableModel.addRow(new Object[]{
                        "<html><b>" + itemTitle + "</b><br><font color='#777'>Bukti: " + proof + "</font></html>",
                        claimant,
                        statusText,
                        "<html><font color='#3498db'><u>Verifikasi</u></font></html>"
                });
            }
        } catch (ServiceException ex) {
            UiKit.error(this, ex.getMessage());
        }
    }

    private void handleVerifikasi(Claim claim) {
        String[] options = {"Setujui (Disetujui)", "Tolak (Ditolak)", "Batal"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Verifikasi klaim untuk barang \"" + (claim.getItemTitle() != null ? claim.getItemTitle() : "#" + claim.getItemId()) + "\"?\n"
                        + "Bukti: " + (claim.getProofDescription() != null ? claim.getProofDescription() : "-"),
                "Verifikasi Klaim",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        String adminId = Session.getAdmin() != null ? Session.getAdmin().getId() : "1";
        try {
            if (choice == 0) {
                claimService.approve(claim.getId(), adminId);
                refreshTable();
                UiKit.info(this, "Klaim berhasil disetujui!");
            } else if (choice == 1) {
                claimService.reject(claim.getId(), adminId);
                refreshTable();
                UiKit.info(this, "Klaim ditolak.");
            }
        } catch (ServiceException ex) {
            UiKit.warn(this, ex.getMessage());
        }
    }
}