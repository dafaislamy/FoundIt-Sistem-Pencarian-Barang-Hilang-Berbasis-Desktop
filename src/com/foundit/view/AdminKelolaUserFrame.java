package com.foundit.view;

import com.foundit.model.User;
import com.foundit.service.ServiceException;
import com.foundit.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Kelola Pengguna Admin terhubung ke UserService (Database)[cite: 43].
 */
public class AdminKelolaUserFrame extends JFrame {

    private final UserService userService = new UserService();
    private List<User> currentUsers = new ArrayList<>();

    private JTextField tfSearch;
    private DefaultTableModel tableModel;
    private JTable table;

    public AdminKelolaUserFrame() {
        setTitle("Kelola Pengguna - FoundIt Admin");
        setSize(1150, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(new AdminSidebarPanel(this, AdminSidebarPanel.Menu.KELOLA_USER), BorderLayout.WEST);

        JPanel mainLayout = new JPanel(new BorderLayout(0, 20));
        mainLayout.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainLayout.setBackground(new Color(247, 249, 252));

        mainLayout.add(AdminSidebarPanel.buildTopbar(
                "Kelola Pengguna",
                "Manajemen data akun mahasiswa dan pengguna aplikasi FoundIt."
        ), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);

        JPanel controlCard = new JPanel(new BorderLayout(12, 0));
        controlCard.setBackground(Color.WHITE);
        controlCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                new EmptyBorder(15, 15, 15, 15)
        ));
        controlCard.setMaximumSize(new Dimension(2000, 75));

        tfSearch = new JTextField();
        tfSearch.setToolTipText("Cari nama, NIM, atau email...");
        tfSearch.addActionListener(e -> refreshTable());

        JButton btnCari = new JButton("Cari");
        btnCari.addActionListener(e -> refreshTable());

        JButton btnTambah = new JButton("+ Tambah User");
        btnTambah.setBackground(new Color(52, 152, 219));
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFocusPainted(false);
        btnTambah.addActionListener(e -> handleTambahUser());

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightButtons.setOpaque(false);
        rightButtons.add(btnCari);
        rightButtons.add(btnTambah);

        controlCard.add(tfSearch, BorderLayout.CENTER);
        controlCard.add(rightButtons, BorderLayout.EAST);
        centerPanel.add(controlCard, BorderLayout.NORTH);

        String[] columns = {"NIM & Nama", "Kontak Email & Telp", "Role", "Aksi"};
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
                if (col == 3 && row >= 0 && row < currentUsers.size()) {
                    handleRowAction(currentUsers.get(row));
                }
            }
        });

        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainLayout.add(centerPanel, BorderLayout.CENTER);
        add(mainLayout, BorderLayout.CENTER);

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        try {
            currentUsers = userService.search(tfSearch.getText());
            for (User u : currentUsers) {
                String nim = u.getNim() != null ? u.getNim() : "-";
                String phone = u.getPhone() != null ? u.getPhone() : "-";
                String role = u.getRole() != null ? u.getRole() : "USER";
                tableModel.addRow(new Object[]{
                        "<html><b>" + u.getFullName() + "</b><br><font color='#777'>NIM: " + nim + "</font></html>",
                        u.getEmail() + "<br><font color='#777'>" + phone + "</font>",
                        role,
                        "<html><font color='#dc3545'><u>Hapus</u></font></html>"
                });
            }
        } catch (ServiceException ex) {
            UiKit.error(this, ex.getMessage());
        }
    }

    private void handleTambahUser() {
        JTextField namaField = new JTextField();
        JTextField nimField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField telpField = new JTextField();
        JPasswordField passField = new JPasswordField();

        Object[] message = {
                "Nama Lengkap:", namaField,
                "NIM:", nimField,
                "Email:", emailField,
                "No Telepon:", telpField,
                "Password:", passField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Tambah Pengguna Baru", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                User u = new User();
                u.setFullName(namaField.getText().trim());
                u.setNim(nimField.getText().trim());
                u.setEmail(emailField.getText().trim());
                u.setPhone(telpField.getText().trim());
                u.setRole("USER");
                userService.save(u, new String(passField.getPassword()));
                refreshTable();
                UiKit.info(this, "Pengguna baru berhasil disimpan ke database!");
            } catch (ServiceException ex) {
                UiKit.warn(this, ex.getMessage());
            }
        }
    }

    private void handleRowAction(User u) {
        if (UiKit.confirm(this, "Yakin ingin menghapus pengguna \"" + u.getFullName() + "\"?")) {
            try {
                userService.delete(u.getId());
                refreshTable();
                UiKit.info(this, "Pengguna berhasil dihapus!");
            } catch (ServiceException ex) {
                UiKit.error(this, ex.getMessage());
            }
        }
    }
}