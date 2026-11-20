package com.foundit.model;

import java.time.LocalDate;

public class Item extends BaseEntity {

    private String title;          // judul
    private String description;    // deskripsi
    private ItemType type = ItemType.FOUND; // tipe
    private String location;       // lokasi
    private LocalDate eventDate;   // tanggal_kejadian
    private String photo;          // foto (nama file)
    private ItemStatus status = ItemStatus.PENDING;
    private String categoryId;     // categories_id
    private String finderName;     // nama_penemu (untuk tipe found)
    private String finderPhone;    // no_telp_penemu
    private String reporterId;     // users_id (pelapor, untuk tipe lost), boleh null

    // Data relasi (hasil JOIN) - hanya untuk ditampilkan
    private String categoryName;
    private String reporterName;

    public Item() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getFinderName() {
        return finderName;
    }

    public void setFinderName(String finderName) {
        this.finderName = finderName;
    }

    public String getFinderPhone() {
        return finderPhone;
    }

    public void setFinderPhone(String finderPhone) {
        this.finderPhone = finderPhone;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    @Override
    public String toString() {
        return "Item[id=" + getId() + ", judul=" + title + ", status=" + status + "]";
    }
}
