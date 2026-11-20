package com.foundit.view;

import com.foundit.model.Category;
import com.foundit.service.CategoryService;
import com.foundit.service.ServiceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Kelola Kategori Admin terhubung ke CategoryService (Database)[cite: 41].
 */
public class AdminKelolaKategoriFrame extends JFrame {

    private final CategoryService categoryService = new CategoryService();
    private List<Category> currentCategories = new ArrayList<>();

    private JTextField tfNama;
    private JTextArea taDesc;
    private DefaultTableModel tableModel;
    private JTable table;

    public AdminKelolaKategoriFrame() {
        setTitle("Kelola Kategori - FoundIt Admin");
        setSize(1150, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(new AdminSidebarPanel(this, AdminSidebarPanel.Menu.KELOLA_KATEGORI), BorderLayout.WEST);

        JPanel mainLayout = new JPanel(new BorderLayout(0, 25));
        mainLayout.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainLayout.setBackground(new Color(247, 249, 252));

        mainLayout.add(AdminSidebarPanel.buildTopbar(
                "Kelola Kategori Barang",
                "Atur kategori barang untuk mempermudah pencarian dan filter pengguna."
        ), BorderLayout.NORTH);

        JPanel contentGrid = new JPanel(new GridLayout(1, 2, 25, 0));
        contentGrid.setOpaque(false);

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel formTitle = new JLabel("Tambah Kategori Baru");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formCard.add(formTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        formCard.add(new JLabel("Nama Kategori"));
        formCard.add(Box.createRigidArea(new Dimension(0, 8)));
        tfNama = new JTextField();
        tfNama.setMaximumSize(new Dimension(2000, 38));
        formCard.add(tfNama);
        formCard.add(Box.createRigidArea(new Dimension(0, 15)));

        formCard.add(new JLabel("Keterangan / Deskripsi"));
        formCard.add(Box.createRigidArea(new Dimension(0, 8)));
        taDesc = new JTextArea(4, 20);
        taDesc.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        formCard.add(new JScrollPane(taDesc));
        formCard.add(Box.createRigidArea(new Dimension(0, 25)));

        JButton btnSimpan = new JButton("Simpan Kategori");
        btnSimpan.setBackground(new Color(52, 152, 219));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFocusPainted(false);
        btnSimpan.setMaximumSize(new Dimension(2000, 42));
        btnSimpan.addActionListener(e -> handleSimpan());
        formCard.add(btnSimpan);

        contentGrid.add(formCard);

        String[] columns = {"Nama Kategori & Deskripsi", "Aksi"};
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
                if (col == 1 && row >= 0 && row < currentCategories.size()) {
                    handleRowAction(currentCategories.get(row));
                }
            }
        });

        contentGrid.add(new JScrollPane(table));
        mainLayout.add(contentGrid, BorderLayout.CENTER);
        add(mainLayout, BorderLayout.CENTER);

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        try {
            currentCategories = categoryService.findAll();
            for (Category c : currentCategories) {
                String desc = (c.getDescription() != null && !c.getDescription().isBlank())
                        ? c.getDescription()
                        : "-";
                tableModel.addRow(new Object[]{
                        "<html><b>" + c.getName() + "</b><br><font color='#777'>" + desc + "</font></html>",
                        "<html><font color='#3498db'><u>Edit | Hapus</u></font></html>"
                });
            }
        } catch (ServiceException ex) {
            UiKit.error(this, ex.getMessage());
        }
    }

    private void handleSimpan() {
        try {
            Category c = new Category();
            c.setName(tfNama.getText().trim());
            c.setDescription(taDesc.getText().trim());
            categoryService.save(c);
            tfNama.setText("");
            taDesc.setText("");
            refreshTable();
            UiKit.info(this, "Kategori berhasil disimpan ke database!");
        } catch (ServiceException ex) {
            UiKit.warn(this, ex.getMessage());
        }
    }

    private void handleRowAction(Category c) {
        String[] options = {"Edit", "Hapus", "Batal"};
        int choice = JOptionPane.showOptionDialog(
                this, "Pilih aksi untuk kategori \"" + c.getName() + "\":",
                "Kelola Kategori", JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, options, options[0]
        );
        if (choice == 0) {
            String baru = JOptionPane.showInputDialog(this, "Ubah nama kategori:", c.getName());
            if (baru != null) {
                try {
                    c.setName(baru.trim());
                    categoryService.save(c);
                    refreshTable();
                    UiKit.info(this, "Kategori berhasil diperbarui!");
                } catch (ServiceException ex) {
                    UiKit.warn(this, ex.getMessage());
                }
            }
        } else if (choice == 1) {
            if (UiKit.confirm(this, "Yakin ingin menghapus kategori \"" + c.getName() + "\"?")) {
                try {
                    categoryService.delete(c.getId());
                    refreshTable();
                    UiKit.info(this, "Kategori berhasil dihapus!");
                } catch (ServiceException ex) {
                    UiKit.error(this, ex.getMessage());
                }
            }
        }
    }
}