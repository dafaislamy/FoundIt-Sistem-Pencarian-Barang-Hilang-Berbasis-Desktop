package com.foundit.apk.controller;

import com.foundit.apk.model.Category;
import com.foundit.apk.model.CategoryRepository;
import com.foundit.apk.view.AdminKelolaKategoriFrame;
import javax.swing.JOptionPane;

/**
 * Controller untuk AdminKelolaKategoriFrame.
 */
public class AdminKelolaKategoriController {
    private final AdminKelolaKategoriFrame view;
    private final CategoryRepository repo = CategoryRepository.getInstance();

    public AdminKelolaKategoriController() {
        this(new AdminKelolaKategoriFrame(false));
    }

    public AdminKelolaKategoriController(AdminKelolaKategoriFrame view) {
        this.view = view;
        bind();
    }

    public void bind() {
        refreshView();

        view.addSimpanListener(e -> handleSimpan());

        view.addTableActionClickListener(row -> handleRowAction(row));

        view.setMenuNavigationHandler(index -> handleNavigation(index));
    }

    private void refreshView() {
        view.renderTable(repo.getKategoriList());
    }

    private void handleSimpan() {
        String nama = view.getNamaKategori();
        if (!nama.isEmpty()) {
            repo.addCategory(nama);
            refreshView();
            view.setNamaKategori("");
            view.setDeskripsiKategori("");
            JOptionPane.showMessageDialog(view, "Kategori berhasil disimpan!");
        } else {
            JOptionPane.showMessageDialog(view, "Nama kategori wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleRowAction(int row) {
        if (row < 0 || row >= repo.getKategoriList().size()) return;
        Category c = repo.getKategoriList().get(row);

        String[] options = {"Edit", "Hapus", "Batal"};
        int choice = JOptionPane.showOptionDialog(view, "Pilih aksi untuk kategori ini:", "Kelola Kategori",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (choice == 0) {
            String baru = JOptionPane.showInputDialog(view, "Ubah nama kategori:", c.getNamaKategori());
            if (baru != null && !baru.trim().isEmpty()) {
                repo.updateCategory(row, baru.trim());
                refreshView();
                JOptionPane.showMessageDialog(view, "Kategori berhasil diperbarui!");
            }
        } else if (choice == 1) {
            int confirm = JOptionPane.showConfirmDialog(view, "Yakin ingin menghapus kategori ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                repo.removeCategory(row);
                refreshView();
                JOptionPane.showMessageDialog(view, "Kategori berhasil dihapus!");
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

    public AdminKelolaKategoriFrame getView() {
        return view;
    }
}
