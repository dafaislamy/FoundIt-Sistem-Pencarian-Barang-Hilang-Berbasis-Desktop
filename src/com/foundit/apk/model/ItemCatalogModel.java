package com.foundit.apk.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model pencarian dan penyajian katalog barang untuk CariBarangFrame.
 */
public class ItemCatalogModel {
    private final List<String[]> semuaBarang = new ArrayList<>();
    private final ReportRepository reportRepo = ReportRepository.getInstance();

    public ItemCatalogModel() {
        muatData();
    }

    public void muatData() {
        semuaBarang.clear();
        semuaBarang.add(new String[]{
            "Flashdisk SanDisk 64GB", "hilang", "Elektronik", "Hilang di sekitar Fakultas Teknik."
        });
        semuaBarang.add(new String[]{
            "Kunci Motor Yamaha", "ditemukan", "Aksesoris", "Ditemukan di area parkir kampus."
        });
        semuaBarang.add(new String[]{
            "Handphone", "hilang", "Elektronik", "Hilang di area kantin kampus."
        });
        semuaBarang.add(new String[]{
            "Dompet Kulit", "ditemukan", "Aksesoris", "Ditemukan di sekitar perpustakaan."
        });

        for (ReportRepository.Report r : reportRepo.findAll()) {
            String st = "HILANG".equalsIgnoreCase(r.getType()) ? "hilang" : "ditemukan";
            String kat = (r.getCategory() != null && !r.getCategory().isEmpty()) ? r.getCategory() : "Lainnya";
            semuaBarang.add(new String[]{
                r.getName(), st, kat, r.getLocation() + " - " + r.getDescription()
            });
        }
    }

    public List<String[]> filterBarang(String keyword, String statusFilter, String categoryFilter) {
        muatData();
        List<String[]> hasil = new ArrayList<>();
        String kw = keyword == null ? "" : keyword.trim().toLowerCase();
        String sf = statusFilter == null ? "" : statusFilter.toLowerCase();
        String cf = categoryFilter == null ? "" : categoryFilter.toLowerCase();

        for (String[] item : semuaBarang) {
            String nama = item[0].toLowerCase();
            String status = item[1].toLowerCase();
            String kategori = item[2].toLowerCase();

            boolean cocokNama = nama.contains(kw);
            boolean cocokStatus = sf.contains("semua") || status.equals(sf);
            boolean cocokKategori = cf.contains("semua") || kategori.equals(cf);

            if (cocokNama && cocokStatus && cocokKategori) {
                hasil.add(item);
            }
        }
        return hasil;
    }
}
