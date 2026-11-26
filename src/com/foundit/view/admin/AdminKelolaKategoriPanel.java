package com.foundit.view.admin;

import com.foundit.controller.AdminController;
import com.foundit.model.Category;
import com.foundit.service.ServiceException;
import com.foundit.view.ThemeConfig;
import com.foundit.view.components.RoundedButton;
import com.foundit.view.components.RoundedPanel;
import com.foundit.view.components.RoundedTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminKelolaKategoriPanel — CRUD Kategori barang pada tabel MySQL categories.
 */
public class AdminKelolaKategoriPanel extends JPanel {

    private final AdminController controller;
    private RoundedTextField searchField;
    private DefaultTableModel tableModel;
    private JTable table;
    private List<Category> categories = new ArrayList<>();

    public AdminKelolaKategoriPanel(AdminController controller) {
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

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        searchField = new RoundedTextField(8);
        searchField.setPreferredSize(new Dimension(240, 36));
        searchField.addActionListener(e -> loadCategories());

        RoundedButton cariBtn = new RoundedButton("Cari");
        cariBtn.setPreferredSize(new Dimension(80, 36));
        cariBtn.addActionListener(e -> loadCategories());
        left.add(searchField);
        left.add(cariBtn);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        RoundedButton addBtn = new RoundedButton("+ Tambah Kategori");
        addBtn.setPreferredSize(new Dimension(160, 36));
        addBtn.addActionListener(e -> addCategory());

        RoundedButton editBtn = RoundedButton.createOutlineButton("Ubah Nama");
        editBtn.setPreferredSize(new Dimension(115, 36));
        editBtn.addActionListener(e -> editCategory());

        RoundedButton delBtn = RoundedButton.createDangerButton("Hapus");
        delBtn.setPreferredSize(new Dimension(90, 36));
        delBtn.addActionListener(e -> deleteCategory());

        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(delBtn);

        topBar.add(left, BorderLayout.WEST);
        topBar.add(actions, BorderLayout.EAST);
        card.add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID Kategori", "Nama Kategori"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(44);
        table.setFont(ThemeConfig.FONT_MEDIUM);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowVerticalLines(false);
        table.setGridColor(ThemeConfig.TABLE_ROW_BORDER);
        table.getTableHeader().setFont(ThemeConfig.FONT_SEMIBOLD);
        table.getTableHeader().setBackground(ThemeConfig.TABLE_HEADER_BG);
        table.getTableHeader().setForeground(ThemeConfig.TABLE_HEADER_FG);
        table.getTableHeader().setPreferredSize(new Dimension(0, 42));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeConfig.BORDER_CARD));
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    public void refreshData() {
        loadCategories();
    }

    private void loadCategories() {
        tableModel.setRowCount(0);
        try {
            categories = controller.searchCategories(searchField.getText());
            for (Category c : categories) {
                tableModel.addRow(new Object[]{c.getId(), c.getName()});
            }
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat kategori: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addCategory() {
        String name = JOptionPane.showInputDialog(this, "Masukkan nama kategori baru:", "Tambah Kategori", JOptionPane.PLAIN_MESSAGE);
        if (name == null) return;
        try {
            Category c = new Category();
            c.setName(name);
            controller.saveCategory(c);
            loadCategories();
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validasi", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void editCategory() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= categories.size()) {
            JOptionPane.showMessageDialog(this, "Pilih kategori yang ingin diubah.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Category c = categories.get(row);
        String newName = (String) JOptionPane.showInputDialog(this, "Nama kategori baru:", "Ubah Kategori",
                JOptionPane.PLAIN_MESSAGE, null, null, c.getName());
        if (newName == null) return;
        try {
            c.setName(newName);
            controller.saveCategory(c);
            loadCategories();
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validasi", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteCategory() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= categories.size()) {
            JOptionPane.showMessageDialog(this, "Pilih kategori yang ingin dihapus.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Category c = categories.get(row);
        if (JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus kategori \"" + c.getName() + "\"?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                controller.deleteCategory(c.getId());
                loadCategories();
            } catch (ServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal Menghapus", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
