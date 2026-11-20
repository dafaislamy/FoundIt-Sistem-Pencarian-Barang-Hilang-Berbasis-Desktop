package com.foundit.service;

import com.foundit.dao.AdminDAO;
import com.foundit.model.Admin;

public class AdminService extends AccountService<Admin> {

    public AdminService() {
        super(new AdminDAO());
    }

    @Override
    protected void validateExtra(Admin a) {
        // Admin tidak memiliki field tambahan
    }
}
