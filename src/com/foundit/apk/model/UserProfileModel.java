package com.foundit.apk.model;

/**
 * Model penyimpanan state profil pengguna aktif di memori.
 */
public class UserProfileModel {
    private static String namaUser = "Mahasiswa FoundIt";
    private static String nimUser = "25071100361";
    private static String fakultasUser = "Fakultas Teknik";
    private static String jurusanUser = "S1 Teknik Informatika";
    private static String angkatanUser = "2025";
    private static String emailUser = "";
    private static String nohpUser = "081234567890";

    public static String getNamaUser() { return namaUser; }
    public static void setNamaUser(String val) { namaUser = val; }

    public static String getNimUser() { return nimUser; }
    public static void setNimUser(String val) { nimUser = val; }

    public static String getFakultasUser() { return fakultasUser; }
    public static void setFakultasUser(String val) { fakultasUser = val; }

    public static String getJurusanUser() { return jurusanUser; }
    public static void setJurusanUser(String val) { jurusanUser = val; }

    public static String getAngkatanUser() { return angkatanUser; }
    public static void setAngkatanUser(String val) { angkatanUser = val; }

    public static String getEmailUser() { return emailUser; }
    public static void setEmailUser(String val) { emailUser = val; }

    public static String getNohpUser() { return nohpUser; }
    public static void setNohpUser(String val) { nohpUser = val; }

    public static String getInitial(String fallbackEmail) {
        if (namaUser != null && !namaUser.trim().isEmpty()) {
            return namaUser.trim().substring(0, 1).toUpperCase();
        }
        if (fallbackEmail != null && !fallbackEmail.trim().isEmpty()) {
            return fallbackEmail.trim().substring(0, 1).toUpperCase();
        }
        return "U";
    }

    public static void updateProfile(String nama, String nim, String fakultas, String jurusan, String angkatan, String email, String nohp) {
        namaUser = nama;
        nimUser = nim;
        fakultasUser = fakultas;
        jurusanUser = (jurusan == null || jurusan.trim().isEmpty()) ? "-" : jurusan.trim();
        angkatanUser = angkatan;
        emailUser = (email == null || email.trim().isEmpty()) ? "-" : email.trim();
        nohpUser = (nohp == null || nohp.trim().isEmpty()) ? "-" : nohp.trim();
    }
}
