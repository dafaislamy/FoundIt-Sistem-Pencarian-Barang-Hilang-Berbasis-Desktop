package com.foundit.service;

import com.foundit.dao.CrudDAO;
import com.foundit.dao.DAOException;
import com.foundit.model.Account;
import com.foundit.util.PasswordUtil;

import java.util.List;

public abstract class AccountService<T extends Account> extends AbstractService<T> {

    private final CrudDAO<T> dao;
    private final String roleLabel;

    protected AccountService(CrudDAO<T> dao, String roleLabel) {
        this.dao = dao;
        this.roleLabel = roleLabel;
    }

    @Override
    protected CrudDAO<T> dao() {
        return dao;
    }

    public List<T> search(String keyword) throws ServiceException {
        String q = keyword == null ? "" : keyword.trim().toLowerCase();
        try {
            List<T> all = dao.findAll();
            if (q.isEmpty()) {
                return all;
            }
            return all.stream()
                    .filter(a -> contains(a.getId(), q)
                            || contains(a.getFullName(), q)
                            || contains(a.getEmail(), q)
                            || contains(a.getPhone(), q)
                            || matchesExtra(a, q))
                    .toList();
        } catch (DAOException e) {
            throw wrap(e);
        }
    }

    protected boolean matchesExtra(T account, String lowerKeyword) {
        return false;
    }

    public T authenticate(String email, String plainPassword) throws ServiceException {
        String cleanEmail = Validator.email(email);
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new ValidationException("Password wajib diisi.");
        }
        String hashed = PasswordUtil.hash(plainPassword);
        try {
            for (T acc : dao.findAll()) {
                if (acc.getEmail() != null
                        && acc.getEmail().equalsIgnoreCase(cleanEmail)
                        && hashed.equals(acc.getPasswordHash())) {
                    return acc;
                }
            }
        } catch (DAOException e) {
            throw wrap(e);
        }
        throw new ValidationException("Email atau password " + roleLabel + " tidak sesuai.");
    }

    public void save(T account, String plainPassword) throws ServiceException {
        boolean isCreate = account.getId() == null || account.getId().isBlank();
        account.setFullName(Validator.requireText("Nama", account.getFullName()));
        account.setEmail(Validator.email(account.getEmail()));
        account.setPhone(Validator.optionalPhone(account.getPhone()));
        validateExtra(account);

        if (isCreate || (plainPassword != null && !plainPassword.isEmpty())) {
            Validator.password(plainPassword);
            account.setPasswordHash(PasswordUtil.hash(plainPassword));
        } else {
            T existing = findById(account.getId());
            account.setPasswordHash(existing.getPasswordHash());
        }

        try {
            if (isCreate) {
                dao.insert(account);
            } else {
                dao.update(account);
            }
        } catch (DAOException e) {
            throw wrap(e);
        }
    }

    protected void validateExtra(T account) throws ValidationException {
    }
}