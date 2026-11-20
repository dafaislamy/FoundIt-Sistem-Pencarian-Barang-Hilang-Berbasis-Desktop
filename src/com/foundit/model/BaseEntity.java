package com.foundit.model;

import java.time.LocalDateTime;

/** Kelas dasar semua entitas: id (null = belum disimpan) + waktu pembuatan. */
public abstract class BaseEntity implements Identifiable {

    private String id;
    private LocalDateTime createdAt;

    protected BaseEntity() {
    }

    protected BaseEntity(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
