package com.foundit.model;

public class User extends Account {

    private String identityNumber; // nomor_identitas (NIM/NIK), boleh kosong
    private String role = "user";  // enum('user','admin') pada tabel users

    public User() {
    }

    @Override
    public String getAccountType() {
        return "User";
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
