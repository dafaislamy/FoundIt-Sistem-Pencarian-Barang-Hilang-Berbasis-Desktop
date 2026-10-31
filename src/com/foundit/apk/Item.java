package com.foundit.apk;

import java.time.LocalDate;

public class Item extends BaseEntity implements SearchSpecification {
    private String judul;
    private String deskripsi;
    private ItemType tipe;
    private String lokasi;
    private LocalDate tanggalKejadian;
    private String foto;
    private ItemStatus status;
    private String namaPenemu;
    private String noTelpPenemu;

    // Relasi dari UML
    private Category category;
    private User pelapor;

    public Item(String id, String judul, String deskripsi, ItemType tipe, String lokasi,
                LocalDate tanggalKejadian, Category category, User pelapor) {
        super(id);
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.tipe = tipe;
        this.lokasi = lokasi;
        this.tanggalKejadian = tanggalKejadian;
        this.category = category;
        this.pelapor = pelapor;
        this.status = ItemStatus.PENDING; // Status awal default
    }

    public void updateStatus(ItemStatus newStatus) {
        this.status = newStatus;
    }

    public boolean isAvailable() {
        return this.status == ItemStatus.PUBLISHED;
    }

    @Override
    public boolean matches(String keyword) {
        if (keyword == null) return false;
        String query = keyword.toLowerCase();
        return (judul != null && judul.toLowerCase().contains(query)) ||
                (deskripsi != null && deskripsi.toLowerCase().contains(query)) ||
                (lokasi != null && lokasi.toLowerCase().contains(query));
    }

    // Getter dan Setter
    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public ItemType getTipe() { return tipe; }
    public void setTipe(ItemType tipe) { this.tipe = tipe; }

    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }

    public LocalDate getTanggalKejadian() { return tanggalKejadian; }
    public void setTanggalKejadian(LocalDate tanggalKejadian) { this.tanggalKejadian = tanggalKejadian; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public ItemStatus getStatus() { return status; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public User getPelapor() { return pelapor; }
    public void setPelapor(User pelapor) { this.pelapor = pelapor; }
}