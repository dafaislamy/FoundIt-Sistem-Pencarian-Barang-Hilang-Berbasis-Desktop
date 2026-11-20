package com.foundit.model;

/** enum('Pending','Terbit','Selesai','Ditolak','Arsip') pada tabel items. */
public enum ItemStatus {
    PENDING("Pending"), TERBIT("Terbit"), SELESAI("Selesai"), DITOLAK("Ditolak"), ARSIP("Arsip");

    private final String db;

    ItemStatus(String db) {
        this.db = db;
    }

    public String getDb() {
        return db;
    }

    public static ItemStatus fromDb(String value) {
        for (ItemStatus s : values()) {
            if (s.db.equalsIgnoreCase(value)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status barang tidak dikenal: " + value);
    }

    @Override
    public String toString() {
        return db;
    }
}
