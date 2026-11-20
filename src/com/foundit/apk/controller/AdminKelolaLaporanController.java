package com.foundit.apk.controller;

import com.foundit.apk.model.*;
import com.foundit.apk.view.AdminKelolaLaporanFrame;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Controller untuk AdminKelolaLaporanFrame.
 */
public class AdminKelolaLaporanController {
    private final AdminKelolaLaporanFrame view;
    private final List<Item> laporanList = new ArrayList<>();

    public AdminKelolaLaporanController() {
        this(new AdminKelolaLaporanFrame(false));
    }

    public AdminKelolaLaporanController(AdminKelolaLaporanFrame view) {
        this.view = view;
        initDefaultItems();
        bind();
    }

    private void initDefaultItems() {
        Category katElektronik = new Category("Elektronik");
        Category katAksesoris = new Category("Aksesoris");
        User pelapor1 = new User("U1", "Budi Santoso", "220101002", "budi@campus.ac.id", "123", "081234567890", Role.MAHASISWA);
        User pelapor2 = new User("U2", "Sarah Amelia", "230102045", "sarah@campus.ac.id", "123", "089876543211", Role.MAHASISWA);

        Item item1 = new Item("I1", "Flashdisk SanDisk 64GB", "Warna merah hitam", ItemType.LOST, "Gedung C Fakultas Teknik", LocalDate.now(), katElektronik, pelapor1);
        item1.updateStatus(ItemStatus.PUBLISHED);

        Item item2 = new Item("I2", "Kunci Motor Yamaha", "Gantungan kulit coklat", ItemType.FIND, "Area Parkir Timur", LocalDate.now(), katAksesoris, pelapor2);
        item2.updateStatus(ItemStatus.PUBLISHED);

        laporanList.add(item1);
        laporanList.add(item2);
    }

    public void bind() {
        view.renderTable(laporanList);

        view.addSearchDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { view.applyFilter(view.getSearchText()); }
            public void removeUpdate(DocumentEvent e) { view.applyFilter(view.getSearchText()); }
            public void changedUpdate(DocumentEvent e) { view.applyFilter(view.getSearchText()); }
        });

        view.addResetListener(e -> view.resetFilterFields());

        view.addTableActionClickListener(modelRow -> handleTableAction(modelRow));

        view.setMenuNavigationHandler(index -> handleNavigation(index));
    }

    private void handleTableAction(int modelRow) {
        if (modelRow >= 0 && modelRow < laporanList.size()) {
            String[] newStatuses = {"Aktif", "Proses Klaim", "Dikembalikan", "Ditutup"};
            String selected = (String) JOptionPane.showInputDialog(view, "Pilih Status Baru:", "Ubah Status Barang",
                    JOptionPane.QUESTION_MESSAGE, null, newStatuses, newStatuses[0]);
            if (selected != null) {
                laporanList.get(modelRow).updateStatus(ItemStatus.COMPLETED);
                JOptionPane.showMessageDialog(view, "Status barang berhasil diperbarui menjadi: " + selected);
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

    public AdminKelolaLaporanFrame getView() {
        return view;
    }
}
