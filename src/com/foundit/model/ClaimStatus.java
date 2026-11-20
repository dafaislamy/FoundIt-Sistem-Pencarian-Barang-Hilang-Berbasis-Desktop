package com.foundit.model;

/** enum('Menunggu','Disetujui','Ditolak','Dibatalkan') pada tabel claims. */
public enum ClaimStatus {
    MENUNGGU("Menunggu"), DISETUJUI("Disetujui"), DITOLAK("Ditolak"), DIBATALKAN("Dibatalkan");

    private final String db;

    ClaimStatus(String db) {
        this.db = db;
    }

    public String getDb() {
        return db;
    }

    public static ClaimStatus fromDb(String value) {
        for (ClaimStatus s : values()) {
            if (s.db.equalsIgnoreCase(value)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status klaim tidak dikenal: " + value);
    }

    @Override
    public String toString() {
        return db;
    }
}
