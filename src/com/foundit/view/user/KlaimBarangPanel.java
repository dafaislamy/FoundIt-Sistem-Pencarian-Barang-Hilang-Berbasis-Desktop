package com.foundit.view.user;

import com.foundit.controller.UserController;
import com.foundit.model.Account;
import com.foundit.model.Item;
import com.foundit.service.ServiceException;
import com.foundit.util.ImageHelper;
import com.foundit.util.Session;
import com.foundit.view.ThemeConfig;
import com.foundit.view.components.RoundedButton;
import com.foundit.view.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * KlaimBarangPanel — Formulir pengajuan klaim barang temuan (status Terbit).
 * Mendukung pre-selection barang saat user menekan tombol "Klaim" dari Cari Barang,
 * upload foto lampiran bukti kepemilikan ke folder uploads/, dan simpan ke MySQL.
 */
public class KlaimBarangPanel extends JPanel {

    private final UserController controller;
    private final Consumer<String> navigator;

    private JComboBox<ItemOption> itemCombo;
    private JLabel itemPreviewImg;
    private JLabel itemPreviewMeta;
    private JTextArea buktiArea;

    private File selectedProofFile;
    private JLabel fileLabel;
    private JLabel proofPreviewImg;

    private String pendingPreselectItemId;

    public KlaimBarangPanel(UserController controller, Consumer<String> navigator) {
        this.controller = controller;
        this.navigator = navigator;

        setOpaque(false);
        setLayout(new BorderLayout());
        add(buildFormScroll(), BorderLayout.CENTER);
    }

    private JScrollPane buildFormScroll() {
        RoundedPanel card = new RoundedPanel(ThemeConfig.CARD_RADIUS, ThemeConfig.BORDER_CARD, 1, false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(28, 32, 32, 32));

        JLabel title = new JLabel("Ajukan Klaim Kepemilikan Barang");
        title.setFont(ThemeConfig.FONT_TITLE);
        title.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Pilih barang temuan dan jelaskan bukti kepemilikan yang spesifik (ciri khusus, isi, stiker, dll).");
        sub.setFont(ThemeConfig.FONT_REGULAR);
        sub.setForeground(ThemeConfig.COLOR_TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(22));

        // 1. Pilih Barang
        card.add(createLabel("Pilih Barang Temuan *"));
        card.add(Box.createVerticalStrut(6));
        itemCombo = new JComboBox<>();
        itemCombo.setBackground(Color.WHITE);
        itemCombo.setFont(ThemeConfig.FONT_MEDIUM);
        itemCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        itemCombo.setPreferredSize(new Dimension(0, 42));
        itemCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        itemCombo.addActionListener(e -> updateItemPreview());
        card.add(itemCombo);
        card.add(Box.createVerticalStrut(14));

        // Preview Barang terpilih
        RoundedPanel previewBox = new RoundedPanel(10, ThemeConfig.BORDER_CARD, 1, false);
        previewBox.setBackground(ThemeConfig.BG_PAGE);
        previewBox.setLayout(new BorderLayout(14, 0));
        previewBox.setBorder(new EmptyBorder(10, 12, 10, 12));
        previewBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 96));
        previewBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        itemPreviewImg = new JLabel(ImageHelper.createPlaceholderIcon(72, 72));
        itemPreviewImg.setPreferredSize(new Dimension(72, 72));
        itemPreviewMeta = new JLabel("Pilih barang untuk melihat detail singkat.");
        itemPreviewMeta.setFont(ThemeConfig.FONT_REGULAR);
        itemPreviewMeta.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);

        previewBox.add(itemPreviewImg, BorderLayout.WEST);
        previewBox.add(itemPreviewMeta, BorderLayout.CENTER);
        card.add(previewBox);
        card.add(Box.createVerticalStrut(18));

        // 2. Bukti Kepemilikan
        card.add(createLabel("Deskripsi Bukti Kepemilikan *"));
        card.add(Box.createVerticalStrut(6));
        buktiArea = new JTextArea(4, 20);
        buktiArea.setLineWrap(true);
        buktiArea.setWrapStyleWord(true);
        buktiArea.setFont(ThemeConfig.FONT_MEDIUM);
        buktiArea.setBorder(new EmptyBorder(10, 12, 10, 12));

        JScrollPane buktiScroll = new JScrollPane(buktiArea);
        buktiScroll.setBorder(BorderFactory.createLineBorder(ThemeConfig.INPUT_BORDER, 1));
        buktiScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));
        buktiScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(buktiScroll);
        card.add(Box.createVerticalStrut(18));

        // 3. Lampiran Foto Bukti (opsional)
        card.add(createLabel("Lampiran Foto Bukti / Nota / Identitas (Opsional)"));
        card.add(Box.createVerticalStrut(8));

        RoundedPanel uploadBox = new RoundedPanel(10, ThemeConfig.INPUT_BORDER, 1, false);
        uploadBox.setBackground(ThemeConfig.BG_PAGE);
        uploadBox.setLayout(new BorderLayout(16, 0));
        uploadBox.setBorder(new EmptyBorder(12, 14, 12, 14));
        uploadBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 108));
        uploadBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        proofPreviewImg = new JLabel(ImageHelper.createPlaceholderIcon(80, 80));
        proofPreviewImg.setPreferredSize(new Dimension(80, 80));
        uploadBox.add(proofPreviewImg, BorderLayout.WEST);

        JPanel uploadRight = new JPanel();
        uploadRight.setOpaque(false);
        uploadRight.setLayout(new BoxLayout(uploadRight, BoxLayout.Y_AXIS));

        RoundedButton chooseBtn = RoundedButton.createOutlineButton("Pilih Foto Bukti...");
        chooseBtn.setPreferredSize(new Dimension(170, 36));
        chooseBtn.setMaximumSize(new Dimension(170, 36));
        chooseBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        chooseBtn.addActionListener(e -> chooseProofFile());

        fileLabel = new JLabel("Belum ada lampiran dipilih");
        fileLabel.setFont(ThemeConfig.FONT_SMALL);
        fileLabel.setForeground(ThemeConfig.COLOR_TEXT_MUTED);
        fileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        uploadRight.add(chooseBtn);
        uploadRight.add(Box.createVerticalStrut(8));
        uploadRight.add(fileLabel);
        uploadBox.add(uploadRight, BorderLayout.CENTER);
        card.add(uploadBox);
        card.add(Box.createVerticalStrut(24));

        // 4. Submit Button
        RoundedButton submitBtn = new RoundedButton("Ajukan Klaim Barang");
        submitBtn.setMaximumSize(new Dimension(230, 44));
        submitBtn.setPreferredSize(new Dimension(230, 44));
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

    public void preselectItem(String itemId) {
        this.pendingPreselectItemId = itemId;
    }

    public void refreshData() {
        itemCombo.removeAllItems();
        try {
            List<Item> claimables = controller.getClaimableItems();
            ItemOption toSelect = null;
            for (Item it : claimables) {
                ItemOption opt = new ItemOption(it);
                itemCombo.addItem(opt);
                if (pendingPreselectItemId != null && pendingPreselectItemId.equals(it.getId())) {
                    toSelect = opt;
                }
            }
            if (toSelect != null) {
                itemCombo.setSelectedItem(toSelect);
                pendingPreselectItemId = null;
            }
        } catch (ServiceException ignored) {
        }
        updateItemPreview();
    }

    private void updateItemPreview() {
        ItemOption selected = (ItemOption) itemCombo.getSelectedItem();
        if (selected == null) {
            itemPreviewImg.setIcon(ImageHelper.createPlaceholderIcon(72, 72));
            itemPreviewMeta.setText("Belum ada barang temuan yang tersedia untuk diklaim.");
            return;
        }
        Item it = selected.item;
        itemPreviewImg.setIcon(ImageHelper.loadScaledIcon(it.getPhoto(), 72, 72));
        itemPreviewMeta.setText("<html><b>" + it.getTitle() + "</b> (" + it.getId() + ")"
                + "<br><font color='#64748b'>Kategori: " + (it.getCategoryName() != null ? it.getCategoryName() : "-")
                + "  •  Lokasi: " + it.getLocation() + "</font></html>");
    }

    private void chooseProofFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih Lampiran Foto Bukti");
        chooser.setFileFilter(new FileNameExtensionFilter("Gambar (JPG, JPEG, PNG)", "jpg", "jpeg", "png"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedProofFile = chooser.getSelectedFile();
            fileLabel.setText("Terpilih: " + selectedProofFile.getName());
            proofPreviewImg.setIcon(ImageHelper.loadScaledIcon(selectedProofFile.getAbsolutePath(), 80, 80));
        }
    }

    private void handleSubmit() {
        Account acc = Session.getCurrent();
        if (acc == null) return;

        ItemOption opt = (ItemOption) itemCombo.getSelectedItem();
        if (opt == null) {
            JOptionPane.showMessageDialog(this, "Tidak ada barang yang dipilih!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String attachmentName = null;
        if (selectedProofFile != null) {
            try {
                attachmentName = ImageHelper.saveUploadedImage(selectedProofFile, "claim");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan lampiran: " + ex.getMessage(),
                        "Error Upload", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            controller.submitClaim(opt.item.getId(), acc.getId(), buktiArea.getText(), attachmentName);
            JOptionPane.showMessageDialog(this,
                    "Klaim berhasil diajukan! Status klaim saat ini: Menunggu verifikasi Admin.",
                    "Klaim Terkirim", JOptionPane.INFORMATION_MESSAGE);
            buktiArea.setText("");
            selectedProofFile = null;
            fileLabel.setText("Belum ada lampiran dipilih");
            proofPreviewImg.setIcon(ImageHelper.createPlaceholderIcon(80, 80));
            navigator.accept("status-klaim");
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Pengajuan Klaim Gagal", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(ThemeConfig.INPUT_LABEL_FG);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private static class ItemOption {
        final Item item;
        ItemOption(Item item) { this.item = item; }
        @Override public String toString() {
            return item.getId() + " — " + item.getTitle() + " (" + item.getLocation() + ")";
        }
    }
}
