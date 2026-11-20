package com.foundit.model;

public class Category extends BaseEntity {

    private String name; // kolom nama_kategori

    public Category() {
    }

    public Category(String id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Category[id=" + getId() + ", name=" + name + "]";
    }
}
