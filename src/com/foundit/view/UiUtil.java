package com.foundit.view;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/** Utilitas kecil untuk komponen Swing yang dipakai berulang. */
public final class UiUtil {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String TITLE = "FoundIt";
    public static final String ALL = "-- Semua --";

    private UiUtil() {
    }

    public static String format(LocalDateTime time) {
        return time == null ? "-" : DATE_TIME.format(time);
    }

    public static String orDash(String text) {
        return text == null || text.isBlank() ? "-" : text;
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, TITLE + " - Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    public static void warn(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, TITLE + " - Perhatian", JOptionPane.WARNING_MESSAGE);
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, TITLE, JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, TITLE + " - Konfirmasi",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public static JButton button(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        return b;
    }

    /** Isi item combo filter: "-- Semua --" lalu seluruh nilai enum. */
    public static Object[] withAll(Object[] values) {
        Object[] items = new Object[values.length + 1];
        items[0] = ALL;
        System.arraycopy(values, 0, items, 1, values.length);
        return items;
    }

    /** Baris isian nama file: JTextField + tombol "Pilih..." (hanya nama file yang disimpan). */
    public static JPanel fileRow(JTextField field) {
        JPanel row = new JPanel(new BorderLayout(4, 0));
        JButton choose = button("Pilih...");
        choose.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(row) == JFileChooser.APPROVE_OPTION) {
                field.setText(chooser.getSelectedFile().getName());
            }
        });
        row.add(field, BorderLayout.CENTER);
        row.add(choose, BorderLayout.EAST);
        return row;
    }

    /** Isi ulang combo box dengan mempertahankan pilihan sebelumnya (berdasarkan id). */
    public static void fillCombo(JComboBox<Option> combo, List<Option> options, String nullLabel) {
        String previous = selectedId(combo);
        combo.removeAllItems();
        if (nullLabel != null) {
            combo.addItem(new Option(null, nullLabel));
        }
        for (Option o : options) {
            combo.addItem(o);
        }
        select(combo, previous);
    }

    public static void select(JComboBox<Option> combo, String id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (Objects.equals(combo.getItemAt(i).id(), id)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        if (combo.getItemCount() > 0) {
            combo.setSelectedIndex(0);
        }
    }

    /** id terpilih, atau null bila memilih opsi kosong / belum ada pilihan. */
    public static String selectedId(JComboBox<Option> combo) {
        Option o = (Option) combo.getSelectedItem();
        return o == null ? null : o.id();
    }
}
