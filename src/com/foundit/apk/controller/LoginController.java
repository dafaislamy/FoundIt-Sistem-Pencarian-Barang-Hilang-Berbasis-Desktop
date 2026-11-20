package com.foundit.apk.controller;

import com.foundit.apk.view.LoginFrame;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Controller untuk LoginFrame.
 */
public class LoginController {
    private final LoginFrame view;

    public LoginController() {
        this(new LoginFrame(false));
    }

    public LoginController(LoginFrame view) {
        this.view = view;
        bind();
    }

    public void bind() {
        view.addUserChoiceListener(e -> view.selectRole("user"));
        view.addAdminChoiceListener(e -> view.selectRole("admin"));

        view.addPasswordDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updatePasswordRules(); }
            public void removeUpdate(DocumentEvent e) { updatePasswordRules(); }
            public void changedUpdate(DocumentEvent e) { updatePasswordRules(); }
        });

        view.addBackListener(e -> {
            view.dispose();
            new IndexController().init();
        });

        view.addSubmitListener(e -> handleLogin());
    }

    private void updatePasswordRules() {
        String pw = view.getPassword();
        view.setRuleStatus("length", pw.length() >= 8);
        view.setRuleStatus("upper", pw.matches(".*[A-Z].*"));
        view.setRuleStatus("symbol", pw.matches(".*[!@#$%^&*].*"));
    }

    private void handleLogin() {
        String em = view.getEmail();
        String pw = view.getPassword();
        String role = view.getSelectedRole();

        if (em.isEmpty() || !em.contains("@")) {
            JOptionPane.showMessageDialog(
                view,
                "Email wajib diisi dan harus mengandung tanda @.",
                "Email belum valid",
                JOptionPane.WARNING_MESSAGE
            );
            view.focusEmail();
            return;
        }

        if (pw.length() < 8
                || !pw.matches(".*[A-Z].*")
                || !pw.matches(".*[!@#$%^&*].*")) {

            JOptionPane.showMessageDialog(
                view,
                "Password belum memenuhi semua persyaratan.",
                "Password belum valid",
                JOptionPane.WARNING_MESSAGE
            );
            view.focusPassword();
            return;
        }

        view.dispose();
        if (role.equals("admin")) {
            new AdminDashboardController().init();
        } else {
            new UserDashboardController(em, role).init();
        }
    }

    public void init() {
        view.setVisible(true);
    }

    public LoginFrame getView() {
        return view;
    }
}
