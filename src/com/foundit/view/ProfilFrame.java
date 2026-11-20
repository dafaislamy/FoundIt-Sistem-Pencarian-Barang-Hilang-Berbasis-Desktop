package com.foundit.view;

import com.foundit.model.User;
import com.foundit.service.ServiceException;
import com.foundit.service.UserService;
import com.foundit.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProfilFrame extends JFrame {

    private final UserService userService = new UserService();

    private JLabel lblPhoto, lblNamaProfil, lblNimProfil;
    private JLabel valNama, valNim, valEmail, valNohp, valRole;

    public ProfilFrame() {
        UiKit.frame(this, "FoundIt | Profil Saya", 1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setContentPane(build());
        refreshProfile();
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiKit.BG);
        root.add(new UserSidebarPanel(this, UserSidebarPanel.Menu.PROFIL), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(26, 34, 25, 34));
        main.add(UserSidebarPanel.buildTopbar("Profil Saya", "Kelola informasi akun kamu."), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(22, 0, 10, 8));

        JPanel profileHeader = UiKit.card();
        profileHeader.setBackground(Color.WHITE);
        profileHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(237, 240, 242)),
                new EmptyBorder(26, 28, 26, 28)
        ));
        profileHeader.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));
        profileHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 135));

        lblPhoto = UiKit.label(Session.getInitial(), 32, true, Color.WHITE);
        lblPhoto.setHorizontalAlignment(SwingConstants.CENTER);
        lblPhoto.setOpaque(true);
        lblPhoto.setBackground(UiKit.BLUE);
        lblPhoto.setPreferredSize(new Dimension(80, 80));
        profileHeader.add(lblPhoto);

        JPanel headerIdentity = new JPanel();
        headerIdentity.setOpaque(false);
        headerIdentity.setLayout(new BoxLayout(headerIdentity, BoxLayout.Y_AXIS));
        lblNamaProfil = UiKit.label(Session.getDisplayName(), 22, true, UiKit.TEXT);
        lblNimProfil = UiKit.label(Session.getDisplayEmail(), 14, false, UiKit.MUTED);
        headerIdentity.add(lblNamaProfil);
        headerIdentity.add(Box.createVerticalStrut(6));
        headerIdentity.add(lblNimProfil);

        profileHeader.add(headerIdentity);
        body.add(profileHeader);
        body.add(Box.createVerticalStrut(22));

        JPanel profileCard = UiKit.card();
        profileCard.setBackground(Color.WHITE);
        profileCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(237, 240, 242)),
                new EmptyBorder(28, 30, 30, 30)
        ));
        profileCard.setLayout(new BorderLayout(0, 25));

        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);

        JPanel chLeft = new JPanel();
        chLeft.setOpaque(false);
        chLeft.setLayout(new BoxLayout(chLeft, BoxLayout.Y_AXIS));
        chLeft.add(UiKit.label("Informasi Pribadi", 20, true, UiKit.TEXT));
        chLeft.add(Box.createVerticalStrut(5));
        chLeft.add(UiKit.label("Informasi akun yang terdaftar di sistem.", 13, false, UiKit.MUTED));
        cardHeader.add(chLeft, BorderLayout.WEST);

        JButton btnEdit = UiKit.button("Edit Profil", UiKit.BLUE, Color.WHITE);
        btnEdit.setPreferredSize(new Dimension(130, 40));
        btnEdit.addActionListener(e -> promptEditProfile());
        cardHeader.add(btnEdit, BorderLayout.EAST);

        profileCard.add(cardHeader, BorderLayout.NORTH);

        JPanel infoGrid = new JPanel(new GridLayout(3, 2, 18, 18));
        infoGrid.setOpaque(false);

        valNama = UiKit.label("-", 15, true, UiKit.TEXT);
        valNim = UiKit.label("-", 15, true, UiKit.TEXT);
        valEmail = UiKit.label("-", 15, true, UiKit.TEXT);
        valNohp = UiKit.label("-", 15, true, UiKit.TEXT);
        valRole = UiKit.label("-", 15, true, UiKit.TEXT);

        infoGrid.add(createInfoItem("Nama Lengkap", valNama));
        infoGrid.add(createInfoItem("Nomor Identitas", valNim));
        infoGrid.add(createInfoItem("Email", valEmail));
        infoGrid.add(createInfoItem("Nomor Telepon", valNohp));
        infoGrid.add(createInfoItem("Peran Akun", valRole));
        infoGrid.add(new JPanel() {{ setOpaque(false); }});

        profileCard.add(infoGrid, BorderLayout.CENTER);
        body.add(profileCard);

        JScrollPane sc = new JScrollPane(body);
        sc.setBorder(null);
        sc.getViewport().setOpaque(false);
        sc.setOpaque(false);
        sc.getVerticalScrollBar().setUnitIncrement(18);

        main.add(sc, BorderLayout.CENTER);
        root.add(main, BorderLayout.CENTER);
        return root;
    }

    private void refreshProfile() {
        User u = Session.getUser();
        if (u != null) {
            try {
                User fresh = userService.findById(u.getId());
                if (fresh != null) {
                    u = fresh;
                    Session.login(u);
                }
            } catch (ServiceException ignored) {
            }
            lblPhoto.setText(Session.getInitial());
            lblNamaProfil.setText(u.getFullName());
            lblNimProfil.setText(u.getIdentityNumber() != null ? u.getIdentityNumber() : u.getEmail());

            valNama.setText(u.getFullName());
            valNim.setText(u.getIdentityNumber() != null ? u.getIdentityNumber() : "-");
            valEmail.setText(u.getEmail());
            valNohp.setText(u.getPhone() != null ? u.getPhone() : "-");
            valRole.setText(u.getRole() != null ? u.getRole() : "user");
        } else {
            valNama.setText(Session.getDisplayName());
            valEmail.setText(Session.getDisplayEmail());
        }
    }

    private void promptEditProfile() {
        User u = Session.getUser();
        if (u == null) {
            UiKit.warn(this, "Profil ini hanya untuk akun User.");
            return;
        }

        JTextField tfNama = new JTextField(u.getFullName());
        JTextField tfNim = new JTextField(u.getIdentityNumber() != null ? u.getIdentityNumber() : "");
        JTextField tfEmail = new JTextField(u.getEmail());
        JTextField tfHp = new JTextField(u.getPhone() != null ? u.getPhone() : "");
        JPasswordField tfPass = new JPasswordField();

        Object[] fields = {
                "Nama Lengkap:", tfNama,
                "Nomor Identitas:", tfNim,
                "Email:", tfEmail,
                "Nomor Telepon:", tfHp,
                "Password Baru (kosongkan jika tidak diubah):", tfPass
        };

        int opt = JOptionPane.showConfirmDialog(this, fields, "Edit Profil", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                u.setFullName(tfNama.getText().trim());
                u.setIdentityNumber(tfNim.getText().trim());
                u.setEmail(tfEmail.getText().trim());
                u.setPhone(tfHp.getText().trim());
                String newPass = new String(tfPass.getPassword());
                userService.save(u, newPass.isEmpty() ? null : newPass);
                Session.login(u);
                refreshProfile();
                UiKit.info(this, "Profil berhasil diperbarui di database!");
            } catch (ServiceException ex) {
                UiKit.warn(this, ex.getMessage());
            }
        }
    }

    private JPanel createInfoItem(String labelText, JLabel valueLabel) {
        JPanel item = new JPanel();
        item.setBackground(new Color(247, 249, 252));
        item.setBorder(new EmptyBorder(16, 18, 16, 18));
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
        item.add(UiKit.label(labelText, 13, false, new Color(127, 140, 141)));
        item.add(Box.createVerticalStrut(7));
        item.add(valueLabel);
        return item;
    }
}