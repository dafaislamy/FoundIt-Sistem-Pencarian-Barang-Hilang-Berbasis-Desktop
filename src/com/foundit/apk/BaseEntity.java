package com.foundit.apk;

import java.time.LocalDateTime;

public abstract class BaseEntity {
    protected String id;
    protected LocalDateTime createdAt;

    public BaseEntity(String id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}