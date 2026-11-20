package com.foundit.apk.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model penyimpanan dan pengelolaan status siklus barang untuk Admin.
 */
public class AdminItemRepository {
    private static final AdminItemRepository INSTANCE = new AdminItemRepository();
    private final List<String[]> barangList = new ArrayList<>();

    private AdminItemRepository() {
        barangList.add(new String[]{"#BRG-1029", "Flashdisk SanDisk 64GB", "Elektronik", "Rian Pratama", "Teknik Mesin", "Gedung C Fakultas Teknik", "Hilang"});
        barangList.add(new String[]{"#BRG-1034", "Kunci Motor Yamaha Aerox", "Aksesoris", "Budi Santoso", "Teknik Elektro", "Parkir Timur Kampus", "Ditemukan"});
        barangList.add(new String[]{"#BRG-0992", "Dompet Kulit Hitam", "Aksesoris", "Sarah Amelia", "Sistem Informasi", "Perpustakaan Lt. 2", "Dalam Proses Klaim"});
        barangList.add(new String[]{"#BRG-0871", "KTM atas nama Dimas A.", "Dokumen", "Petugas Satpam Rektorat", "Staff", "Lobi Rektorat", "Telah Dikembalikan"});
    }

    public static AdminItemRepository getInstance() {
        return INSTANCE;
    }

    public List<String[]> getBarangList() {
        return barangList;
    }

    public void updateStatus(int index, String newStatus) {
        if (index >= 0 && index < barangList.size()) {
            barangList.get(index)[6] = newStatus;
        }
    }
}
