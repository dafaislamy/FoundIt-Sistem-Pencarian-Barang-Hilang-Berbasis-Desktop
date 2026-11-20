package com.foundit.view;

import com.foundit.model.User;
import com.foundit.service.UserService;

import javax.swing.JComboBox;
import javax.swing.JTextField;

public class UserPanel extends AccountPanel<User> {

    private final JTextField identityField = new JTextField(20);
    private final JComboBox<String> roleCombo = new JComboBox<>(new String[]{"user", "admin"});

    public UserPanel() {
        super(new UserService(), User::new,
                new String[]{"ID", "Nama", "No. Identitas", "Email", "Telepon", "Role", "Dibuat"});
        build("Form Pengguna");
    }

    @Override
    protected Object[] toRow(User u) {
        return new Object[]{u.getId(), u.getFullName(), UiUtil.orDash(u.getIdentityNumber()), u.getEmail(),
                UiUtil.orDash(u.getPhone()), u.getRole(), UiUtil.format(u.getCreatedAt())};
    }

    @Override
    protected void buildExtraFields() {
        addField("No. Identitas", identityField);
        addField("Role", roleCombo);
    }

    @Override
    protected void fillExtra(User u) {
        identityField.setText(u.getIdentityNumber() == null ? "" : u.getIdentityNumber());
        roleCombo.setSelectedItem(u.getRole());
    }

    @Override
    protected void resetExtra() {
        identityField.setText("");
        roleCombo.setSelectedItem("user");
    }

    @Override
    protected void readExtra(User u) {
        u.setIdentityNumber(identityField.getText());
        u.setRole((String) roleCombo.getSelectedItem());
    }
}
