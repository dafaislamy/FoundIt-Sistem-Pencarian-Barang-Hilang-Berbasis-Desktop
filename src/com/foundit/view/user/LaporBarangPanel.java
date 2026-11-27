package com.foundit.view.user;

import com.foundit.controller.UserController;
import com.foundit.model.Account;
import com.foundit.model.Category;
import com.foundit.model.ItemType;
import com.foundit.service.ServiceException;
import com.foundit.util.ImageHelper;
import com.foundit.util.Session;
import com.foundit.view.ThemeConfig;
import com.foundit.view.components.RoundedButton;
import com.foundit.view.components.RoundedPanel;
import com.foundit.view.components.RoundedTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Consumer;

/**
 * LaporBarangPanel — Form Lapor Barang Hilang & Lapor Barang Ditemukan.
 * Mendukung pemilihan & upload file gambar ke folder lokal uploads/ proyek
 * serta penyimpanan langsung ke database MySQL (status awal Pending).
 */
public class LaporBarangPanel extends JPanel {

    private final UserController controller;
    private final ItemType itemType;
    private final Consumer<String> navigator;

    private RoundedTextField judulField;
    private JComboBox<CategoryOption> kategoriCombo;
    private RoundedTextField lokasiField;
    private RoundedTextField tanggalField;
    private JTextArea deskripsiArea;
    private RoundedTextField penemuNamaField;
    private RoundedTextField penemuTelpField;

    private File selectedImageFile;
    private JLabel fileNameLabel;
    private JLabel previewLabel;

    public LaporBarangPanel(UserController controller, ItemType itemType, Consumer<String> navigator) {
        this.controller = controller;
        this.itemType = itemType;
        this.navigator = navigator;

        setOpaque(false);
        setLayout(new BorderLayout());
        add(buildFormScroll(), BorderLayout.CENTER);
    }

    private JScrollPane buildFormScroll() {
        RoundedPanel card = new RoundedPanel(ThemeConfig.CARD_RADIUS, ThemeConfig.BORDER_CARD, 1, false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(28, 32, 32, 32));

        boolean isFound = (itemType == ItemType.FOUND);

        JLabel title = new JLabel(isFound ? "Formulir Laporan Barang Ditemukan" : "Formulir Laporan Barang Hilang");
        title.setFont(ThemeConfig.FONT_TITLE);
        title.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Lengkapi data barang di bawah ini. Laporan akan ditinjau admin sebelum diterbitkan.");
        sub.setFont(ThemeConfig.FONT_REGULAR);
        sub.setForeground(ThemeConfig.COLOR_TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(22));

        // 1. Judul Barang
        judulField = createField("Contoh: Flashdisk Sandisk 64GB Hitam");
        addRow(card, "Judul / Nama Barang *", judulField);

        // 2. Kategori & Tanggal (2 kolom)
        JPanel row2 = new JPanel(new GridLayout(1, 2, 16, 0));
        row2.setOpaque(false);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
        row2.setAlignmentX(Component.LEFT_ALIGNMENT);

        kategoriCombo = new JComboBox<>();
        kategoriCombo.setBackground(Color.WHITE);
        kategoriCombo.setFont(ThemeConfig.FONT_MEDIUM);
        kategoriCombo.setPreferredSize(new Dimension(0, 42));
        kategoriCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        kategoriCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        tanggalField = createField("yyyy-MM-dd");
        tanggalField.setText(LocalDate.now().toString());

        row2.add(wrap("Kategori Barang *", kategoriCombo));
        row2.add(wrap(isFound ? "Tanggal Ditemukan (YYYY-MM-DD) *" : "Tanggal Hilang (YYYY-MM-DD) *", tanggalField));
        card.add(row2);
        card.add(Box.createVerticalStrut(16));

        // 3. Lokasi
        lokasiField = createField(isFound ? "Contoh: Kantin Kampus / Lab Komputer" : "Contoh: Gedung Kuliah A Lt. 2");
        addRow(card, isFound ? "Lokasi Ditemukan *" : "Lokasi Terakhir / Hilang *", lokasiField);

        // 4. Info Penemu (khusus Barang Ditemukan)
        if (isFound) {
            JPanel rowPenemu = new JPanel(new GridLayout(1, 2, 16, 0));
            rowPenemu.setOpaque(false);
            rowPenemu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
            rowPenemu.setAlignmentX(Component.LEFT_ALIGNMENT);

            penemuNamaField = createField("Nama lengkap penemu");
            penemuTelpField = createField("Contoh: 081234567890");

            rowPenemu.add(wrap("Nama Penemu *", penemuNamaField));
            rowPenemu.add(wrap("No. WhatsApp / Telepon Penemu", penemuTelpField));
            card.add(rowPenemu);
            card.add(Box.createVerticalStrut(16));
        }

        // 5. Deskripsi
        card.add(createLabel("Deskripsi Detail & Ciri-ciri Barang *"));
        card.add(Box.createVerticalStrut(6));
        deskripsiArea = new JTextArea(4, 20);
        deskripsiArea.setLineWrap(true);
        deskripsiArea.setWrapStyleWord(true);
        deskripsiArea.setFont(ThemeConfig.FONT_MEDIUM);
        deskripsiArea.setBorder(new EmptyBorder(10, 12, 10, 12));

        JScrollPane descScroll = new JScrollPane(deskripsiArea);
        descScroll.setBorder(BorderFactory.createLineBorder(ThemeConfig.INPUT_BORDER, 1));
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(descScroll);
        card.add(Box.createVerticalStrut(18));

        // 6. Upload Foto Barang
        card.add(createLabel("Foto Barang (JPG / PNG)"));
        card.add(Box.createVerticalStrut(8));
        card.add(buildUploadBox());
        card.add(Box.createVerticalStrut(24));

        // 7. Submit Button
        RoundedButton submitBtn = new RoundedButton(
                isFound ? "Kirim Laporan Barang Ditemukan" : "Kirim Laporan Barang Hilang",
                isFound ? RoundedButton.Variant.PRIMARY : RoundedButton.Variant.DANGER,
                ThemeConfig.BTN_RADIUS_NORMAL
        );
        submitBtn.setMaximumSize(new Dimension(280, 44));
        submitBtn.setPreferredSize(new Dimension(280, 44));
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitBtn.addActionListener(e -> handleSubmit());
        card.add(submitBtn);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.add(card, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(outer);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel buildUploadBox() {
        RoundedPanel box = new RoundedPanel(10, ThemeConfig.INPUT_BORDER, 1, false);
        box.setBackground(ThemeConfig.BG_PAGE);
        box.setLayout(new BorderLayout(16, 0));
        box.setBorder(new EmptyBorder(12, 14, 12, 14));
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);

        previewLabel = new JLabel(ImageHelper.createPlaceholderIcon(84, 84));
        previewLabel.setPreferredSize(new Dimension(84, 84));
        box.add(previewLabel, BorderLayout.WEST);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        fileNameLabel = new JLabel("Belum ada file dipilih (menggunakan foto default bila kosong)");
        fileNameLabel.setFont(ThemeConfig.FONT_SMALL);
        fileNameLabel.setForeground(ThemeConfig.COLOR_TEXT_MUTED);
        fileNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton chooseBtn = RoundedButton.createOutlineButton("Pilih File Gambar...");
        chooseBtn.setPreferredSize(new Dimension(170, 36));
        chooseBtn.setMaximumSize(new Dimension(170, 36));
        chooseBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        chooseBtn.addActionListener(e -> chooseImage());

        right.add(chooseBtn);
        right.add(Box.createVerticalStrut(8));
        right.add(fileNameLabel);
        box.add(right, BorderLayout.CENTER);
        return box;
    }

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih Foto Barang");
        chooser.setFileFilter(new FileNameExtensionFilter("Gambar (JPG, JPEG, PNG)", "jpg", "jpeg", "png"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = chooser.getSelectedFile();
            fileNameLabel.setText("Terpilih: " + selectedImageFile.getName());
            previewLabel.setIcon(ImageHelper.loadScaledIcon(selectedImageFile.getAbsolutePath(), 84, 84));
        }
    }

    public void refreshData() {
        kategoriCombo.removeAllItems();
        try {
            List<Category> categories = controller.getAllCategories();
            for (Category c : categories) {
                kategoriCombo.addItem(new CategoryOption(c.getId(), c.getName()));
            }
        } catch (ServiceException ignored) {
        }
        Account acc = Session.getCurrent();
        if (itemType == ItemType.FOUND && acc != null && penemuNamaField != null && penemuNamaField.getText().isBlank()) {
            penemuNamaField.setText(acc.getFullName() != null ? acc.getFullName() : "");
            if (acc.getPhone() != null && penemuTelpField != null) {
                penemuTelpField.setText(acc.getPhone());
            }
        }
    }

    private void handleSubmit() {
        Account acc = Session.getCurrent();
        if (acc == null) {
            JOptionPane.showMessageDialog(ThemeConfig.windowOf(this), "Sesi login berakhir.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CategoryOption cat = (CategoryOption) kategoriCombo.getSelectedItem();
        if (cat == null) {
            JOptionPane.showMessageDialog(ThemeConfig.windowOf(this), "Pilih kategori terlebih dahulu!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate tanggal;
        try {
            tanggal = LocalDate.parse(tanggalField.getText().trim());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(ThemeConfig.windowOf(this), "Format tanggal harus YYYY-MM-DD (contoh: 2026-11-26).",
                    "Validasi Tanggal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String savedPhoto = "default.jpg";
        if (selectedImageFile != null) {
            try {
                savedPhoto = ImageHelper.saveUploadedImage(selectedImageFile, "item");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(ThemeConfig.windowOf(this), "Gagal menyimpan gambar: " + ex.getMessage(),
                        "Error Upload", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            if (itemType == ItemType.LOST) {
                controller.submitLostItem(
                        judulField.getText(),
                        cat.id,
                        lokasiField.getText(),
                        tanggal,
                        deskripsiArea.getText(),
                        savedPhoto,
                        acc.getId()
                );
            } else {
                controller.submitFoundItem(
                        judulField.getText(),
                        cat.id,
                        lokasiField.getText(),
                        tanggal,
                        deskripsiArea.getText(),
                        penemuNamaField.getText(),
                        penemuTelpField.getText(),
                        savedPhoto,
                        acc.getId()
                );
            }

            JOptionPane.showMessageDialog(ThemeConfig.windowOf(this),
                    "Laporan berhasil dikirim dengan status Pending dan menunggu verifikasi Admin.",
                    "Laporan Terkirim", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            navigator.accept("riwayat-laporan");
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(ThemeConfig.windowOf(this), ex.getMessage(), "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void resetForm() {
        judulField.setText("");
        lokasiField.setText("");
        tanggalField.setText(LocalDate.now().toString());
        deskripsiArea.setText("");
        selectedImageFile = null;
        fileNameLabel.setText("Belum ada file dipilih (menggunakan foto default bila kosong)");
        previewLabel.setIcon(ImageHelper.createPlaceholderIcon(84, 84));
    }

    private RoundedTextField createField(String tooltip) {
        RoundedTextField tf = new RoundedTextField(8);
        tf.setPreferredSize(new Dimension(0, 42));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        tf.setToolTipText(tooltip);
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tf;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(ThemeConfig.INPUT_LABEL_FG);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel wrap(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(createLabel(label), BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void addRow(JPanel container, String label, JComponent field) {
        container.add(createLabel(label));
        container.add(Box.createVerticalStrut(6));
        container.add(field);
        container.add(Box.createVerticalStrut(16));
    }

    private static class CategoryOption {
        final String id;
        final String name;
        CategoryOption(String id, String name) {
            this.id = id;
            this.name = name;
        }
        @Override public String toString() { return name; }
    }
}
