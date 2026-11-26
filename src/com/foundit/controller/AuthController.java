package com.foundit.controller;

import com.foundit.dao.DAOException;
import com.foundit.dao.UserDAO;
import com.foundit.model.Account;
import com.foundit.model.Admin;
import com.foundit.model.User;
import com.foundit.service.AdminService;
import com.foundit.service.ServiceException;
import com.foundit.service.UserService;
import com.foundit.service.ValidationException;
import com.foundit.util.Session;

import java.util.regex.Pattern;

/**
 * AuthController — menangani logika autentikasi (Login, Register, Logout)
 * sesuai alur dan validasi pada versi web FoundIt (login.php & register.php).
 */
public class AuthController {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserService userService = new UserService();
    private final AdminService adminService = new AdminService();
    private final UserDAO userDAO = new UserDAO();

    /**
     * Proses login membedakan role "user" dan "admin" seperti di login.php.
     */
    public Account login(String email, String password, String role) throws ServiceException {
        String cleanEmail = email == null ? "" : email.trim();
        String cleanPassword = password == null ? "" : password;
        String cleanRole = role == null ? "" : role.trim().toLowerCase();

        if (cleanEmail.isEmpty() || cleanPassword.isEmpty() || cleanRole.isEmpty()) {
            throw new ValidationException("Semua kolom harus diisi!");
        }
        if (!"user".equals(cleanRole) && !"admin".equals(cleanRole)) {
            throw new ValidationException("Pilihan akun tidak valid!");
        }

        if ("admin".equals(cleanRole)) {
            Admin admin = adminService.authenticate(cleanEmail, cleanPassword);
            Session.login(admin);
            return admin;
        } else {
            User user = userService.authenticate(cleanEmail, cleanPassword);
            if (!"user".equalsIgnoreCase(user.getRole())) {
                throw new ValidationException("Jenis akun tidak sesuai.");
            }
            Session.login(user);
            return user;
        }
    }

    /**
     * Proses registrasi akun User baru sesuai validasi pada user/register.php & js/register.js.
     */
    public User registerUser(
            String nama,
            String nim,
            String fakultas,
            String jurusan,
            String angkatan,
            String email,
            String noHp,
            String password
    ) throws ServiceException {

        String cleanNama = nama == null ? "" : nama.trim();
        String cleanNim = nim == null ? "" : nim.trim();
        String cleanFakultas = fakultas == null ? "" : fakultas.trim();
        String cleanJurusan = jurusan == null ? "" : jurusan.trim();
        String cleanAngkatan = angkatan == null ? "" : angkatan.trim();
        String cleanEmail = email == null ? "" : email.trim();
        String cleanNoHp = noHp == null ? "" : noHp.trim();
        String rawPassword = password == null ? "" : password;

        if (cleanNama.isEmpty() || cleanNim.isEmpty() || cleanFakultas.isEmpty()
                || cleanJurusan.isEmpty() || cleanAngkatan.isEmpty()
                || cleanEmail.isEmpty() || cleanNoHp.isEmpty() || rawPassword.isEmpty()) {
            throw new ValidationException("Semua data wajib diisi!");
        }

        if (!EMAIL_PATTERN.matcher(cleanEmail).matches()) {
            throw new ValidationException("Format email tidak valid!");
        }

        if (!isPasswordValid(rawPassword)) {
            throw new ValidationException("Password minimal 8 karakter, memiliki huruf kapital dan simbol!");
        }

        // Cek duplikasi Email atau NIM pada tabel users
        try {
            for (User existing : userDAO.findAll()) {
                if (existing.getEmail() != null && existing.getEmail().equalsIgnoreCase(cleanEmail)) {
                    throw new ValidationException("Email atau NIM sudah terdaftar!");
                }
                if (existing.getIdentityNumber() != null && existing.getIdentityNumber().equalsIgnoreCase(cleanNim)) {
                    throw new ValidationException("Email atau NIM sudah terdaftar!");
                }
            }
        } catch (DAOException e) {
            throw new ServiceException("Registrasi gagal: " + e.getMessage(), e);
        }

        User newUser = new User();
        newUser.setFullName(cleanNama);
        newUser.setIdentityNumber(cleanNim);
        newUser.setEmail(cleanEmail);
        newUser.setPhone(cleanNoHp);
        newUser.setRole("user");

        userService.save(newUser, rawPassword);
        return newUser;
    }

    /** Validasi kekuatan password identik dengan register.php & js/register.js */
    public static boolean isPasswordValid(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasSymbol = password.matches(".*[^A-Za-z0-9].*");
        return hasUpper && hasSymbol;
    }

    /** Logout akun aktif dari Session. */
    public void logout() {
        Session.logout();
    }
}
