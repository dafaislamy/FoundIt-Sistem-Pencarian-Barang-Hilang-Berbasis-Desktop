package com.foundit.model;

/** Kelas dasar akun (User dan Admin): kolom nama, email, no_telp, password. */
public abstract class Account extends BaseEntity {

    private String fullName;
    private String email;
    private String phone;
    private String passwordHash;

    protected Account() {
    }

    protected Account(String id, String fullName, String email, String phone) {
        super(id);
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    /** Jenis akun untuk tampilan (polimorfisme). */
    public abstract String getAccountType();

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public String toString() {
        return getAccountType() + "[id=" + getId() + ", nama=" + fullName + ", email=" + email + "]";
    }
}
