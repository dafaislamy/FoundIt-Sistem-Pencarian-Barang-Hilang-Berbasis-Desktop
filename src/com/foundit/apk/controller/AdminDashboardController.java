package com.foundit.apk.controller;

import com.foundit.apk.view.AdminDashboardFrame;

/**
 * Controller untuk AdminDashboardFrame.
 */
public class AdminDashboardController {
    private final AdminDashboardFrame view;

    public AdminDashboardController() {
        this(new AdminDashboardFrame(false));
    }

    public AdminDashboardController(AdminDashboardFrame view) {
        this.view = view;
        bind();
    }

    public void bind() {
        view.setMenuNavigationHandler(index -> handleNavigation(index));
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

    public AdminDashboardFrame getView() {
        return view;
    }
}
