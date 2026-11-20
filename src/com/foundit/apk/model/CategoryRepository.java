package com.foundit.apk.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model penyimpanan data kategori barang untuk Admin.
 */
public class CategoryRepository {
    private static final CategoryRepository INSTANCE = new CategoryRepository();
    private final List<Category> kategoriList = new ArrayList<>();

    private CategoryRepository() {
        kategoriList.add(new Category("Elektronik"));
        kategoriList.add(new Category("Aksesoris"));
        kategoriList.add(new Category("Dokumen"));
        kategoriList.add(new Category("Lainnya"));
    }

    public static CategoryRepository getInstance() {
        return INSTANCE;
    }

    public List<Category> getKategoriList() {
        return kategoriList;
    }

    public void addCategory(String nama) {
        kategoriList.add(new Category(nama));
    }

    public void updateCategory(int index, String namaBaru) {
        if (index >= 0 && index < kategoriList.size()) {
            kategoriList.get(index).setNamaKategori(namaBaru);
        }
    }

    public void removeCategory(int index) {
        if (index >= 0 && index < kategoriList.size()) {
            kategoriList.remove(index);
        }
    }
}
