package com.foundit.view;

/** Item untuk JComboBox: id (boleh null = "semua"/"kosong") + label tampilan. */
public record Option(String id, String label) {

    @Override
    public String toString() {
        return label;
    }
}
