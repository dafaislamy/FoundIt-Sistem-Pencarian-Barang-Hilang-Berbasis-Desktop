package com.foundit.apk.controller;

import com.foundit.apk.model.ItemCatalogModel;
import com.foundit.apk.view.CariBarangFrame;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Controller untuk CariBarangFrame.
 */
public class CariBarangController {
    private final CariBarangFrame view;
    private final String email;
    private final String role;
    private final ItemCatalogModel catalogModel = new ItemCatalogModel();

    public CariBarangController(String email, String role) {
        this(new CariBarangFrame(email, role, false), email, role);
    }

    public CariBarangController(CariBarangFrame view, String email, String role) {
        this.view = view;
        this.email = email;
        this.role = role;
        bind();
    }

    public void bind() {
        view.addSearchDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { onSearchChanged(); }
            public void removeUpdate(DocumentEvent e) { onSearchChanged(); }
            public void changedUpdate(DocumentEvent e) { onSearchChanged(); }
        });

        view.addClearSearchListener(e -> {
            view.setSearchKeyword("");
            view.focusSearch();
        });

        view.addStatusFilterListener(e -> performFilter());
        view.addCategoryFilterListener(e -> performFilter());

        view.addResetFilterListener(e -> {
            view.setSearchKeyword("");
            view.setSelectedStatusIndex(0);
            view.setSelectedKategoriIndex(0);
            performFilter();
            view.focusSearch();
        });

        view.setMenuAction("Dashboard", () -> {
            view.dispose();
            new UserDashboardController(email, role).init();
        });
        view.setMenuAction("Lapor Barang Hilang", () -> {
            view.dispose();
            new ReportController(email, role, "HILANG", "Lapor Barang Hilang").init();
        });
        view.setMenuAction("Lapor Barang Ditemukan", () -> {
            view.dispose();
            new ReportController(email, role, "DITEMUKAN", "Lapor Barang Ditemukan").init();
        });
        view.setMenuAction("Klaim Barang", () -> {
            view.dispose();
            new KlaimController(email, role).init();
        });
        view.setMenuAction("Status Klaim", () -> {
            view.dispose();
            new StatusKlaimController(email, role).init();
        });
        view.setMenuAction("Riwayat Laporan", () -> {
            view.dispose();
            new RiwayatLaporanController(email, role).init();
        });
        view.setMenuAction("Profil", () -> {
            view.dispose();
            new ProfilController(email, role).init();
        });
        view.setMenuAction("Keluar", () -> {
            view.dispose();
            new IndexController().init();
        });

        performFilter();
    }

    private void onSearchChanged() {
        view.setClearButtonVisible(!view.getSearchKeyword().isEmpty());
        performFilter();
    }

    private void performFilter() {
        String kw = view.getSearchKeyword();
        String st = view.getSelectedStatus();
        String kat = view.getSelectedKategori();

        List<String[]> filtered = catalogModel.filterBarang(kw, st, kat);

        view.renderGrid(filtered,
            item -> JOptionPane.showMessageDialog(
                view,
                "Nama Barang: " + item[0]
                    + "\nStatus: " + ("hilang".equalsIgnoreCase(item[1]) ? "Hilang" : "Ditemukan")
                    + "\nKategori: " + item[2]
                    + "\nKeterangan: " + item[3],
                "Detail Barang",
                JOptionPane.INFORMATION_MESSAGE
            ),
            item -> {
                view.dispose();
                new KlaimController(email, role, item[0], item[2], item[3]).init();
            }
        );
    }

    public void init() {
        view.setVisible(true);
    }

    public CariBarangFrame getView() {
        return view;
    }
}
