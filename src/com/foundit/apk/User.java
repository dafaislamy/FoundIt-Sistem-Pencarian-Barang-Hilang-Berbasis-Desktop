package com.foundit.apk;

public class User extends BaseEntity {
    private String nama;
    private String nomorIdentitas; // NIM / NIP
    private String email;
    private String password;
    private String noTelp;
    private Role role;

    public User(String id, String nama, String nomorIdentitas, String email, String password, String noTelp, Role role) {
        super(id);
        this.nama = nama;
        this.nomorIdentitas = nomorIdentitas;
        this.email = email;
        this.password = password;
        this.noTelp = noTelp;
        this.role = role;
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public boolean verifyPassword(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    // Getter dan Setter
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getNomorIdentitas() { return nomorIdentitas; }
    public void setNomorIdentitas(String nomorIdentitas) { this.nomorIdentitas = nomorIdentitas; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNoTelp() { return noTelp; }
    public void setNoTelp(String noTelp) { this.noTelp = noTelp; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}