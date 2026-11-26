package com.foundit.view.user;

import com.foundit.controller.UserController;
import com.foundit.model.User;
import com.foundit.service.ServiceException;
import com.foundit.util.Session;
import com.foundit.view.ThemeConfig;
import com.foundit.view.components.AvatarLabel;
import com.foundit.view.components.RoundedButton;
import com.foundit.view.components.RoundedPanel;
import com.foundit.view.components.RoundedTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * ProfilPanel — Halaman Profil & Edit Profil User terhubung ke MySQL via UserController.
 */
public class ProfilPanel extends JPanel {

    private final UserController controller;
    private final Runnable onProfileUpdated;

    private AvatarLabel avatarLabel;
    private JLabel headerNameLabel;
    private JLabel headerRoleLabel;

    private RoundedTextField namaField;
    private RoundedTextField nimField;
    private RoundedTextField emailField;
    private RoundedTextField telpField;
    private JPasswordField passwordBaruField;

    public ProfilPanel(UserController controller, Runnable onProfileUpdated) {
        this.controller = controller;
        this.onProfileUpdated = onProfileUpdated;

        setOpaque(false);
        setLayout(new BorderLayout());
        add(buildContent(), BorderLayout.CENTER);
    }

    private JScrollPane buildContent() {
        RoundedPanel card = new RoundedPanel(ThemeConfig.CARD_RADIUS, ThemeConfig.BORDER_CARD, 1, false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(28, 32, 32, 32));

        // Header Avatar
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        avatarLabel = new AvatarLabel("U", 60);

        JPanel hInfo = new JPanel();
        hInfo.setOpaque(false);
        hInfo.setLayout(new BoxLayout(hInfo, BoxLayout.Y_AXIS));
        headerNameLabel = new JLabel("User FoundIt");
        headerNameLabel.setFont(ThemeConfig.FONT_TITLE);
        headerNameLabel.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);
        headerRoleLabel = new JLabel("Mahasiswa / User");
        headerRoleLabel.setFont(ThemeConfig.FONT_REGULAR);
        headerRoleLabel.setForeground(ThemeConfig.COLOR_TEXT_MUTED);
        hInfo.add(headerNameLabel);
        hInfo.add(Box.createVerticalStrut(4));
        hInfo.add(headerRoleLabel);

        header.add(avatarLabel);
        header.add(hInfo);

        card.add(header);
        card.add(Box.createVerticalStrut(24));

        // Form fields
        namaField = createField();
        nimField = createField();
        emailField = createField();
        telpField = createField();

        addRow(card, "Nama Lengkap", namaField);
        addRow(card, "NIM / Nomor Identitas", nimField);
        addRow(card, "Email", emailField);
        addRow(card, "Nomor WhatsApp / Telepon", telpField);

        card.add(createLabel("Password Baru (kosongkan jika tidak ingin mengubah)"));
        card.add(Box.createVerticalStrut(6));
        passwordBaruField = new JPasswordField();
        passwordBaruField.setFont(ThemeConfig.FONT_MEDIUM);
        passwordBaruField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConfig.INPUT_BORDER),
                new EmptyBorder(8, 12, 8, 12)
        ));
        passwordBaruField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        passwordBaruField.setPreferredSize(new Dimension(0, 42));
        passwordBaruField.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(passwordBaruField);
        card.add(Box.createVerticalStrut(24));

        RoundedButton saveBtn = new RoundedButton("Simpan Perubahan Profil");
        saveBtn.setPreferredSize(new Dimension(220, 44));
        saveBtn.setMaximumSize(new Dimension(220, 44));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> handleSave());
        card.add(saveBtn);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.add(card, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(outer);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    public void refreshData() {
        User u = Session.getUser();
        if (u == null) return;
        avatarLabel.setInisial(u.getFullName());
        headerNameLabel.setText(u.getFullName());
        headerRoleLabel.setText(u.getEmail() + "  •  ID: " + u.getId());

        namaField.setText(u.getFullName() != null ? u.getFullName() : "");
        nimField.setText(u.getIdentityNumber() != null ? u.getIdentityNumber() : "");
        emailField.setText(u.getEmail() != null ? u.getEmail() : "");
        telpField.setText(u.getPhone() != null ? u.getPhone() : "");
        passwordBaruField.setText("");
    }

    private void handleSave() {
        User u = Session.getUser();
        if (u == null) return;

        u.setFullName(namaField.getText().trim());
        u.setIdentityNumber(nimField.getText().trim());
        u.setEmail(emailField.getText().trim());
        u.setPhone(telpField.getText().trim());

        String newPwd = new String(passwordBaruField.getPassword());
        try {
            controller.updateUserProfile(u, newPwd.isEmpty() ? null : newPwd);
            Session.login(u);
            refreshData();
            if (onProfileUpdated != null) onProfileUpdated.run();
            JOptionPane.showMessageDialog(this, "Profil berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal Memperbarui Profil", JOptionPane.WARNING_MESSAGE);
        }
    }

    private RoundedTextField createField() {
        RoundedTextField tf = new RoundedTextField(8);
        tf.setPreferredSize(new Dimension(0, 42));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tf;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(ThemeConfig.INPUT_LABEL_FG);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void addRow(JPanel container, String label, JComponent field) {
        container.add(createLabel(label));
        container.add(Box.createVerticalStrut(6));
        container.add(field);
        container.add(Box.createVerticalStrut(16));
    }
}
