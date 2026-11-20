package com.foundit.apk.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model penyimpanan pengajuan klaim barang pengguna.
 */
public class ClaimRepository {
    private static final List<String[]> daftarKlaim = new ArrayList<>();
    private static boolean initialized = false;

    static {
        initDefaults();
    }

    public static synchronized void initDefaults() {
        if (!initialized) {
            daftarKlaim.add(new String[]{
                "Kunci Motor Yamaha Aerox",
                "Menunggu Verifikasi",
                "Aksesoris",
                "Parkir Timur Kampus",
                "Gantungan karet hitam logo Yamaha & stiker biru",
                "Tertinggal di dashboard motor saat selesai kuliah sore"
            });
            daftarKlaim.add(new String[]{
                "Tumbler Biru 600 ml",
                "Klaim Disetujui",
                "Lainnya",
                "Kantin FMIPA",
                "Merk Corkcicle warna biru navy, ada goresan di tutup",
                "Tertinggal di meja pojok kanan dekat kasir"
            });
            initialized = true;
        }
    }

    public static synchronized List<String[]> getDaftarKlaim() {
        initDefaults();
        return daftarKlaim;
    }

    public static synchronized void tambahKlaim(String namaBarang, String kategori, String lokasi, String ciri, String keterangan) {
        initDefaults();
        daftarKlaim.add(new String[]{
            namaBarang,
            "Menunggu Verifikasi",
            kategori,
            lokasi,
            ciri,
            keterangan
        });
    }

    public static synchronized int getJumlahKlaim() {
        initDefaults();
        return daftarKlaim.size();
    }
}
