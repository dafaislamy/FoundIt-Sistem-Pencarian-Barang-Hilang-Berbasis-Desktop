package com.foundit.view;

import com.foundit.model.Item;
import com.foundit.model.ItemStatus;
import com.foundit.service.ItemService;
import com.foundit.service.ServiceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
                "Periksa keabsahan laporan yang masih menunggu verifikasi sebelum dipublikasikan."
        ), BorderLayout.NORTH);

        String[] columns = {"Barang & Deskripsi", "Lokasi", "Status", "Aksi"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(10, 6));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));

        configureColumns();

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

        table.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustRowHeights();
            }
        });

        mainLayout.add(new JScrollPane(table), BorderLayout.CENTER);
        add(mainLayout, BorderLayout.CENTER);

        refreshTable();
    }

    private void configureColumns() {
        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setPreferredWidth(460);
        cm.getColumn(1).setPreferredWidth(210);
        cm.getColumn(2).setPreferredWidth(110);
        cm.getColumn(3).setPreferredWidth(90);

        cm.getColumn(0).setCellRenderer((tbl, value, isSelected, hasFocus, row, column) -> {
            JPanel cell = new JPanel();
            cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
            cell.setBorder(new EmptyBorder(8, 8, 8, 8));
            cell.setBackground(isSelected ? tbl.getSelectionBackground() : Color.WHITE);

            Item it = row < currentItems.size() ? currentItems.get(row) : null;
            String title = it != null && it.getTitle() != null ? it.getTitle() : "-";
            String desc = it != null && it.getDescription() != null ? it.getDescription() : "";

            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblTitle.setForeground(isSelected ? tbl.getSelectionForeground() : new Color(33, 37, 41));
            lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextArea taDesc = new JTextArea(desc);
            taDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            taDesc.setForeground(isSelected ? tbl.getSelectionForeground() : new Color(119, 119, 119));
            taDesc.setBackground(isSelected ? tbl.getSelectionBackground() : Color.WHITE);
            taDesc.setLineWrap(true);
            taDesc.setWrapStyleWord(true);
            taDesc.setEditable(false);
            taDesc.setFocusable(false);
            taDesc.setBorder(null);
            taDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

            cell.add(lblTitle);
            cell.add(Box.createRigidArea(new Dimension(0, 3)));
            cell.add(taDesc);
            return cell;
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                setVerticalAlignment(SwingConstants.CENTER);
                setHorizontalAlignment(column == 3 ? SwingConstants.CENTER : SwingConstants.LEFT);
                if (!isSelected) {
                    setBackground(Color.WHITE);
                }
                return c;
            }
        };

        for (int col = 1; col < cm.getColumnCount(); col++) {
            cm.getColumn(col).setCellRenderer(centerRenderer);
        }
    }

    private void adjustRowHeights() {
        int colWidth = table.getColumnModel().getColumn(0).getWidth();
        if (colWidth <= 0) {
            colWidth = 440;
        }
        int textWidth = Math.max(160, colWidth - 20);

        for (int row = 0; row < currentItems.size(); row++) {
            Item it = currentItems.get(row);
            String title = it.getTitle() != null ? it.getTitle() : "";
            String desc = it.getDescription() != null ? it.getDescription() : "";

            JLabel titleProbe = new JLabel(title);
            titleProbe.setFont(new Font("Segoe UI", Font.BOLD, 13));
            int titleHeight = titleProbe.getPreferredSize().height;

            JTextArea descProbe = new JTextArea(desc);
            descProbe.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            descProbe.setLineWrap(true);
            descProbe.setWrapStyleWord(true);
            descProbe.setSize(new Dimension(textWidth, Short.MAX_VALUE));
            int descHeight = descProbe.getPreferredSize().height;

            int neededHeight = Math.max(60, titleHeight + descHeight + 24);
            table.setRowHeight(row, neededHeight);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        currentItems = new ArrayList<>();
        try {
            List<Item> allItems = itemService.findAll();
            for (Item it : allItems) {
                if (it.getStatus() == ItemStatus.PENDING) {
                    currentItems.add(it);
                    String title = it.getTitle() != null ? it.getTitle() : "-";
                    String loc = it.getLocation() != null ? it.getLocation() : "-";
                    String st = it.getStatus() != null ? it.getStatus().toString() : "-";

                    tableModel.addRow(new Object[]{
                            title,
                            loc,
                            st,
                            "<html><font color='#3498db'><u>Verifikasi</u></font></html>"
                    });
                }
            }
            SwingUtilities.invokeLater(this::adjustRowHeights);
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