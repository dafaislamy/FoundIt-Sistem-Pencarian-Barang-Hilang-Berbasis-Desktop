package com.foundit.view;

import com.foundit.model.Admin;
import com.foundit.service.AdminService;

public class AdminPanel extends AccountPanel<Admin> {

    public AdminPanel() {
        super(new AdminService(), Admin::new, new String[]{"ID", "Nama", "Email", "Telepon", "Dibuat"});
        build("Form Admin");
    }

    @Override
    protected Object[] toRow(Admin a) {
        return new Object[]{a.getId(), a.getFullName(), a.getEmail(), UiUtil.orDash(a.getPhone()),
                UiUtil.format(a.getCreatedAt())};
    }

    @Override
    protected void buildExtraFields() {
        // Admin tidak memiliki field tambahan
    }

    @Override
    protected void fillExtra(Admin a) {
    }

    @Override
    protected void resetExtra() {
    }

    @Override
    protected void readExtra(Admin a) {
    }
}
