package com.foundit.view.admin;

import com.foundit.controller.AdminController;
import com.foundit.model.User;
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
 * AdminKelolaUserPanel — Pengelolaan akun pengguna (Tambah, Ubah, Hapus, Cari)
 * terhubung langsung ke tabel MySQL users.
 */
public class AdminKelolaUserPanel extends JPanel {

    private final AdminController controller;
    private RoundedTextField searchField;
    private DefaultTableModel tableModel;
    private JTable table;
    private List<User> users = new ArrayList<>();

    public AdminKelolaUserPanel(AdminController controller) {
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
        searchField.addActionListener(e -> loadUsers());

        RoundedButton cariBtn = new RoundedButton("Cari");
        cariBtn.setPreferredSize(new Dimension(80, 36));
        cariBtn.addActionListener(e -> loadUsers());
        left.add(searchField);
        left.add(cariBtn);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        RoundedButton addBtn = new RoundedButton("+ Tambah User");
        addBtn.setPreferredSize(new Dimension(135, 36));
        addBtn.addActionListener(e -> openUserDialog(null));

        RoundedButton editBtn = RoundedButton.createOutlineButton("Ubah");
        editBtn.setPreferredSize(new Dimension(85, 36));
        editBtn.addActionListener(e -> editSelected());

        RoundedButton delBtn = RoundedButton.createDangerButton("Hapus");
        delBtn.setPreferredSize(new Dimension(85, 36));
        delBtn.addActionListener(e -> deleteSelected());

        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(delBtn);

        topBar.add(left, BorderLayout.WEST);
        topBar.add(actions, BorderLayout.EAST);
        card.add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Nama Lengkap", "NIM / Identitas", "Email", "No. Telepon", "Role"};
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
        loadUsers();
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        try {
            users = controller.searchUsers(searchField.getText());
            for (User u : users) {
                tableModel.addRow(new Object[]{
                        u.getId(),
                        u.getFullName(),
                        u.getIdentityNumber() != null ? u.getIdentityNumber() : "-",
                        u.getEmail(),
                        u.getPhone() != null ? u.getPhone() : "-",
                        u.getRole()
                });
            }
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat user: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= users.size()) {
            JOptionPane.showMessageDialog(this, "Pilih akun user terlebih dahulu.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        openUserDialog(users.get(row));
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= users.size()) {
            JOptionPane.showMessageDialog(this, "Pilih akun user yang ingin dihapus.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        User u = users.get(row);
        if (JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus akun \"" + u.getFullName() + "\"?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                controller.deleteUser(u.getId());
                loadUsers();
            } catch (ServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal Menghapus", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void openUserDialog(User existing) {
        boolean isEdit = existing != null;
        JTextField namaF = new JTextField(isEdit ? existing.getFullName() : "");
        JTextField nimF = new JTextField(isEdit && existing.getIdentityNumber() != null ? existing.getIdentityNumber() : "");
        JTextField emailF = new JTextField(isEdit ? existing.getEmail() : "");
        JTextField telpF = new JTextField(isEdit && existing.getPhone() != null ? existing.getPhone() : "");
        JComboBox<String> roleC = new JComboBox<>(new String[]{"user", "admin"});
        if (isEdit && existing.getRole() != null) roleC.setSelectedItem(existing.getRole());
        JPasswordField passF = new JPasswordField();

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.add(new JLabel("Nama Lengkap:"));
        form.add(namaF);
        form.add(new JLabel("NIM / Identitas:"));
        form.add(nimF);
        form.add(new JLabel("Email:"));
        form.add(emailF);
        form.add(new JLabel("No. Telepon:"));
        form.add(telpF);
        form.add(new JLabel("Role:"));
        form.add(roleC);
        form.add(new JLabel(isEdit ? "Password Baru (opsional):" : "Password (min 6 kar):"));
        form.add(passF);

        int res = JOptionPane.showConfirmDialog(this, form,
                isEdit ? "Ubah Akun User " + existing.getId() : "Tambah User Baru",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            User target = isEdit ? existing : new User();
            target.setFullName(namaF.getText().trim());
            target.setIdentityNumber(nimF.getText().trim());
            target.setEmail(emailF.getText().trim());
            target.setPhone(telpF.getText().trim());
            target.setRole(String.valueOf(roleC.getSelectedItem()));
            String pwd = new String(passF.getPassword());

            try {
                controller.saveUser(target, pwd.isEmpty() ? null : pwd);
                loadUsers();
            } catch (ServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validasi", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
