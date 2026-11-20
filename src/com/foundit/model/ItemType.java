package com.foundit.model;

/** enum('lost','found') pada tabel items. */
public enum ItemType {
    LOST("lost", "Hilang"), FOUND("found", "Ditemukan");

    private final String db;
    private final String label;

    ItemType(String db, String label) {
        this.db = db;
        this.label = label;
    }

    public String getDb() {
        return db;
    }

    public static ItemType fromDb(String value) {
        for (ItemType t : values()) {
            if (t.db.equalsIgnoreCase(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipe barang tidak dikenal: " + value);
    }

    @Override
    public String toString() {
        return label;
    }
}
