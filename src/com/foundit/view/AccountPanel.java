package com.foundit.view;

import com.foundit.model.Account;
import com.foundit.service.AccountService;
import com.foundit.service.ServiceException;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.util.List;
import java.util.function.Supplier;

/**
 * Panel CRUD generik untuk akun (User dan Admin): nama, email, telepon, password.
 * Subclass menambahkan field khusus lewat hook *Extra dan harus memanggil build(...).
 */
public abstract class AccountPanel<T extends Account> extends AbstractCrudPanel<T> {

    private final AccountService<T> service;
    private final Supplier<T> factory;

    private final JTextField nameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    protected AccountPanel(AccountService<T> service, Supplier<T> factory, String[] columns) {
        super(columns);
        this.service = service;
        this.factory = factory;
    }

    protected abstract void buildExtraFields();

    protected abstract void fillExtra(T account);

    protected abstract void resetExtra();

    protected abstract void readExtra(T account);

    @Override
    protected List<T> loadData(String keyword) throws ServiceException {
        return service.search(keyword);
    }

    @Override
    protected void buildForm() {
        addField("Nama *", nameField);
        addField("Email *", emailField);
        addField("No. Telepon", phoneField);
        buildExtraFields();
        addField("Password", passwordField);
        addField("", new JLabel("<html><small>Min. 6 karakter. Saat Ubah, kosongkan jika<br>"
                + "password tidak ingin diganti.</small></html>"));
    }

    @Override
    protected void fillForm(T a) {
        nameField.setText(a.getFullName());
        emailField.setText(a.getEmail());
        phoneField.setText(a.getPhone() == null ? "" : a.getPhone());
        fillExtra(a);
        passwordField.setText("");
    }

    @Override
    protected void resetForm() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        resetExtra();
        passwordField.setText("");
        nameField.requestFocusInWindow();
    }

    @Override
    protected T readForm(String id) {
        T a = factory.get();
        a.setId(id);
        a.setFullName(nameField.getText());
        a.setEmail(emailField.getText());
        a.setPhone(phoneField.getText());
        readExtra(a);
        return a;
    }

    @Override
    protected void persist(T a) throws ServiceException {
        service.save(a, new String(passwordField.getPassword()));
    }

    @Override
    protected void remove(T a) throws ServiceException {
        service.delete(a.getId());
    }
}
