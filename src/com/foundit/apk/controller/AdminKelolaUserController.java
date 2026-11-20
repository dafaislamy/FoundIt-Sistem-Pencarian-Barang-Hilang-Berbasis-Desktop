package com.foundit.apk.controller;

import com.foundit.apk.model.UserRepository;
import com.foundit.apk.view.AdminKelolaUserFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Controller untuk AdminKelolaUserFrame.
 */
public class AdminKelolaUserController {
    private final AdminKelolaUserFrame view;
    private final UserRepository repo = UserRepository.getInstance();

    public AdminKelolaUserController() {
        this(new AdminKelolaUserFrame(false));
    }

    public AdminKelolaUserController(AdminKelolaUserFrame view) {
        this.view = view;
        bind();
    }

    public void bind() {
        refreshView();

        view.addTambahListener(e -> handleTambahUser());

        view.addTableActionClickListener(row -> handleRowAction(row));

        view.setMenuNavigationHandler(index -> handleNavigation(index));
    }

    private void refreshView() {
        view.renderTable(repo.getUserList());
    }

    private void handleTambahUser() {
        JTextField namaField = new JTextField();
        JTextField nimField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField telpField = new JTextField();
        Object[] message = {
            "Nama Lengkap:", namaField,
            "NIM:", nimField,
            "Email:", emailField,
            "No Telepon:", telpField
        };
        int option = JOptionPane.showConfirmDialog(view, message, "Tambah Pengguna Baru", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String n = namaField.getText().trim();
            String id = nimField.getText().trim();
            String em = emailField.getText().trim();
            String hp = telpField.getText().trim();

            if (!n.isEmpty() && !id.isEmpty()) {
                repo.addUser(n, id, em, hp);
                refreshView();
                JOptionPane.showMessageDialog(view, "Pengguna baru berhasil ditambahkan!");
            } else {
                JOptionPane.showMessageDialog(view, "Nama dan NIM wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void handleRowAction(int row) {
        if (row < 0 || row >= repo.getUserList().size()) return;

        String[] options = {"Ubah Status (Aktif/Nonaktif)", "Hapus User", "Batal"};
        int choice = JOptionPane.showOptionDialog(view, "Pilih aksi untuk pengguna ini:", "Kelola User",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (choice == 0) {
            JOptionPane.showMessageDialog(view, "Status akun pengguna berhasil diubah!");
        } else if (choice == 1) {
            int confirm = JOptionPane.showConfirmDialog(view, "Yakin ingin menghapus pengguna ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                repo.removeUser(row);
                refreshView();
                JOptionPane.showMessageDialog(view, "Pengguna berhasil dihapus!");
            }
        }
    }

    public void handleNavigation(int index) {
        view.dispose();
        if (index == 0) {
            new AdminDashboardController().init();
        } else if (index == 1) {
            new AdminKelolaKategoriController().init();
        } else if (index == 2) {
            new AdminKelolaLaporanController().init();
        } else if (index == 3) {
            new AdminKelolaUserController().init();
        } else if (index == 4) {
            new AdminUbahStatusController().init();
        } else if (index == 5) {
            new AdminVerifikasiKlaimController().init();
        } else if (index == 6) {
            new AdminVerifikasiLaporanController().init();
        }
    }

    public void init() {
        view.setVisible(true);
    }

    public AdminKelolaUserFrame getView() {
        return view;
    }
}
