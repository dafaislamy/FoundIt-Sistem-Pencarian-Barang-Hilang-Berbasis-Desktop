package com.foundit.apk.controller;

import com.foundit.apk.model.AdminItemRepository;
import com.foundit.apk.view.AdminUbahStatusFrame;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Controller untuk AdminUbahStatusFrame.
 */
public class AdminUbahStatusController {
    private final AdminUbahStatusFrame view;
    private final AdminItemRepository repo = AdminItemRepository.getInstance();
    private final List<Integer> displayedIndices = new ArrayList<>();

    public AdminUbahStatusController() {
        this(new AdminUbahStatusFrame(false));
    }

    public AdminUbahStatusController(AdminUbahStatusFrame view) {
        this.view = view;
        bind();
    }

    public void bind() {
        refreshTable();

        view.addSearchDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshTable(); }
            public void removeUpdate(DocumentEvent e) { refreshTable(); }
            public void changedUpdate(DocumentEvent e) { refreshTable(); }
        });

        view.addStatusFilterListener(e -> refreshTable());

        view.addTableActionClickListener(viewRow -> handleRowAction(viewRow));

        view.setMenuNavigationHandler(index -> handleNavigation(index));
    }

    private void refreshTable() {
        displayedIndices.clear();
        String keyword = view.getSearchKeyword().toLowerCase();
        int filterIdx = view.getSelectedStatusFilterIndex();

        String filterVal = "";
        if (filterIdx == 1) filterVal = "hilang";
        else if (filterIdx == 2) filterVal = "ditemukan";
        else if (filterIdx == 3) filterVal = "klaim";
        else if (filterIdx == 4) filterVal = "dikembalikan";

        List<String[]> barangList = repo.getBarangList();
        List<String[]> matching = new ArrayList<>();

        for (int i = 0; i < barangList.size(); i++) {
            String[] b = barangList.get(i);
            String rowText = (b[0] + " " + b[1] + " " + b[2] + " " + b[3] + " " + b[4] + " " + b[5] + " " + b[6]).toLowerCase();

            boolean matchKeyword = keyword.isEmpty() || rowText.contains(keyword);
            boolean matchFilter = filterVal.isEmpty() || b[6].toLowerCase().contains(filterVal);

            if (matchKeyword && matchFilter) {
                displayedIndices.add(i);
                matching.add(b);
            }
        }

        view.renderTable(matching);
    }

    private void handleRowAction(int viewRow) {
        if (viewRow < 0 || viewRow >= displayedIndices.size()) return;
        int actualIndex = displayedIndices.get(viewRow);
        String[] item = repo.getBarangList().get(actualIndex);

        view.promptUpdateStatusDialog(item, statusBersih -> {
            repo.updateStatus(actualIndex, statusBersih);
            refreshTable();
            JOptionPane.showMessageDialog(view, "Status barang berhasil diperbarui!");
        });
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

    public AdminUbahStatusFrame getView() {
        return view;
    }
}
