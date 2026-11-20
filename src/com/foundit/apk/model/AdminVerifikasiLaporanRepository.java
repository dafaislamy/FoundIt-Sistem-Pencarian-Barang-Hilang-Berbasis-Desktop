package com.foundit.apk.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model penyimpanan antrean verifikasi laporan barang untuk Admin.
 */
public class AdminVerifikasiLaporanRepository {
    private static final AdminVerifikasiLaporanRepository INSTANCE = new AdminVerifikasiLaporanRepository();
    private final List<String[]> laporanList = new ArrayList<>();

    private AdminVerifikasiLaporanRepository() {
        // Format: {Tipe, Waktu, Judul Barang, Pelapor, Info Nim/Fakultas, Kategori, Label Lokasi, Lokasi, Deskripsi, StatusDisetujui}
        laporanList.add(new String[]{
            "Laporan Kehilangan", "Diajukan 2 jam yang lalu", "MacBook Air M2 2023",
            "Sarah Amelia", "NIM: 230102045 - FASILKOM", "Elektronik", "Lokasi Hilang:", "Kantin Utama",
            "Warna Space Grey, ada goresan tipis di bagian bawah pojok kanan dan stiker GitHub.", "false"
        });
        laporanList.add(new String[]{
            "Laporan Ditemukan", "Diajukan 4 jam yang lalu", "Kunci Motor Honda Vario",
            "Budi Santoso", "NIM: 220101002 - FT", "Aksesoris", "Lokasi Ditemukan:", "Parkiran Gedung C",
            "Kunci kontak remote hitam dengan gantungan tali merah bertuliskan 'One Heart'.", "false"
        });
    }

    public static AdminVerifikasiLaporanRepository getInstance() {
        return INSTANCE;
    }

    public List<String[]> getLaporanList() {
        return laporanList;
    }

    public void removeLaporan(int index) {
        if (index >= 0 && index < laporanList.size()) {
            laporanList.remove(index);
        }
    }

    public void setApproved(int index, boolean approved) {
        if (index >= 0 && index < laporanList.size()) {
            laporanList.get(index)[9] = String.valueOf(approved);
        }
    }
}
