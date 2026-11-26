package com.foundit.service;

import com.foundit.dao.CrudDAO;
import com.foundit.dao.DAOException;
import com.foundit.model.Account;
import com.foundit.util.PasswordUtil;

public abstract class AccountService<T extends Account> extends AbstractService<T> {

    protected AccountService(CrudDAO<T> dao) {
        super(dao);
    }

    @Override
    protected void validate(T entity) throws ValidationException {
        entity.setFullName(Validator.required("Nama", entity.getFullName(), 100));
        entity.setEmail(Validator.email(entity.getEmail()));
        entity.setPhone(Validator.optionalPhone("Nomor telepon", entity.getPhone()));
        validateExtra(entity);
    }

    protected abstract void validateExtra(T entity) throws ValidationException;

    public T authenticate(String email, String plainPassword) throws ServiceException {
        String cleanEmail = Validator.email(email);
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new ValidationException("Password wajib diisi.");
        }
        boolean emailFound = false;
        try {
            for (T acc : dao.findAll()) {
                if (acc.getEmail() != null && acc.getEmail().equalsIgnoreCase(cleanEmail)) {
                    emailFound = true;
                    if (PasswordUtil.matches(plainPassword, acc.getPasswordHash())) {
                        return acc;
                    }
                }
            }
        } catch (DAOException e) {
            System.err.println("[AUTH ERROR] Gagal membaca data akun dari database: " + e.getMessage());
            throw new ServiceException("Koneksi atau query database gagal: " + e.getMessage(), e);
        }
        if (emailFound) {
            System.err.println("[AUTH FAILED] Password tidak cocok untuk email: " + cleanEmail);
        } else {
            System.err.println("[AUTH FAILED] Email tidak ditemukan: " + cleanEmail);
        }
        throw new ValidationException("Email atau password tidak sesuai.");
    }

    public void save(T account, String plainPassword) throws ServiceException {
        boolean isCreate = account.getId() == null || account.getId().isBlank();
        validate(account);

        if (isCreate || (plainPassword != null && !plainPassword.isEmpty())) {
            String pwd = plainPassword == null ? "" : plainPassword.trim();
            if (pwd.length() < 6) {
                throw new ValidationException("Password minimal 6 karakter.");
            }
            account.setPasswordHash(PasswordUtil.hash(pwd));
        } else {
            T existing = findById(account.getId());
            if (existing != null) {
                account.setPasswordHash(existing.getPasswordHash());
            }
        }

        try {
            if (isCreate) {
                dao.insert(account);
            } else if (!dao.update(account)) {
                throw new ServiceException("Data akun tidak ditemukan atau gagal diperbarui.");
            }
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}