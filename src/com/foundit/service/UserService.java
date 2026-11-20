package com.foundit.service;

import com.foundit.dao.UserDAO;
import com.foundit.model.User;

public class UserService extends AccountService<User> {

    public UserService() {
        super(new UserDAO());
    }

    @Override
    protected void validateExtra(User u) throws ValidationException {
        u.setIdentityNumber(Validator.optional("Nomor identitas", u.getIdentityNumber(), 20));
        String role = u.getRole() == null ? "user" : u.getRole().trim().toLowerCase();
        if (!role.equals("user") && !role.equals("admin")) {
            throw new ValidationException("Role harus 'user' atau 'admin'.");
        }
        u.setRole(role);
    }
}
