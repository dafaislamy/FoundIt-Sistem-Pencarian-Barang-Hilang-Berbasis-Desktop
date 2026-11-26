package com.foundit.view.user;

import com.foundit.controller.UserController;
import com.foundit.model.Category;
import com.foundit.model.Item;
import com.foundit.model.ItemType;
import com.foundit.service.ServiceException;
import com.foundit.util.ImageHelper;
import com.foundit.view.ThemeConfig;
import com.foundit.view.components.RoundedButton;
import com.foundit.view.components.RoundedPanel;
import com.foundit.view.components.RoundedTextField;
import com.foundit.view.components.StatusBadge;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

/**
 * CariBarangPanel — Katalog pencarian & filter barang hilang/ditemukan (status Terbit).
 * Tampilan filter bar, grid card 3 kolom, badge tipe & kategori, tombol Detail & Klaim
 * disesuaikan 1:1 dengan user/cari-barang.php & css/main.css.
 */
public class CariBarangPanel extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final UserController controller;
    private final Consumer<String> onClaimRequested;

    private RoundedTextField searchField;
    private JComboBox<String> tipeCombo;
    private JComboBox<CategoryItem> kategoriCombo;
    private JPanel gridPanel;
    private JLabel countLabel;

    public CariBarangPanel(UserController controller, Consumer<String> onClaimRequested) {
        this.controller = controller;
        this.onClaimRequested = onClaimRequested;

        setOpaque(false);
        setLayout(new BorderLayout(0, 18));

        add(buildFilterBar(), BorderLayout.NORTH);
        add(buildGridScroll(), BorderLayout.CENTER);
    }

    private RoundedPanel buildFilterBar() {
        RoundedPanel bar = new RoundedPanel(ThemeConfig.CARD_RADIUS, ThemeConfig.BORDER_CARD, 1, false);
        bar.setLayout(new BorderLayout(12, 10));
        bar.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel controls = new JPanel(new GridBagLayout());
        controls.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;

        // 1. Search Input
        gbc.gridx = 0;
        gbc.weightx = 2.0;
        searchField = new RoundedTextField(8);
        searchField.setPreferredSize(new Dimension(240, 42));
        searchField.setToolTipText("Cari judul, deskripsi, atau lokasi barang...");
        searchField.addActionListener(e -> loadItems());
        controls.add(searchField, gbc);

        // 2. Tipe ComboBox
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        tipeCombo = new JComboBox<>(new String[]{"Semua Tipe", "Barang Hilang", "Barang Ditemukan"});
        tipeCombo.setPreferredSize(new Dimension(160, 42));
        tipeCombo.setBackground(Color.WHITE);
        tipeCombo.setFont(ThemeConfig.FONT_MEDIUM);
        tipeCombo.addActionListener(e -> loadItems());
        controls.add(tipeCombo, gbc);

        // 3. Kategori ComboBox
        gbc.gridx = 2;
        gbc.weightx = 0.8;
        kategoriCombo = new JComboBox<>();
        kategoriCombo.setPreferredSize(new Dimension(170, 42));
        kategoriCombo.setBackground(Color.WHITE);
        kategoriCombo.setFont(ThemeConfig.FONT_MEDIUM);
        kategoriCombo.addActionListener(e -> loadItems());
        controls.add(kategoriCombo, gbc);

        // 4. Cari Button
        gbc.gridx = 3;
        gbc.weightx = 0;
        RoundedButton searchBtn = new RoundedButton("Cari");
        searchBtn.setPreferredSize(new Dimension(90, 42));
        searchBtn.addActionListener(e -> loadItems());
        controls.add(searchBtn, gbc);

        // 5. Reset Button
        gbc.gridx = 4;
        gbc.insets = new Insets(0, 0, 0, 0);
        RoundedButton resetBtn = new RoundedButton("Reset", RoundedButton.Variant.RESET, 8);
        resetBtn.setPreferredSize(new Dimension(90, 42));
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            tipeCombo.setSelectedIndex(0);
            if (kategoriCombo.getItemCount() > 0) kategoriCombo.setSelectedIndex(0);
            loadItems();
        });
        controls.add(resetBtn, gbc);

        countLabel = new JLabel("Menampilkan 0 barang");
        countLabel.setFont(ThemeConfig.FONT_SMALL);
        countLabel.setForeground(ThemeConfig.COLOR_TEXT_MUTED);

        bar.add(controls, BorderLayout.CENTER);
        bar.add(countLabel, BorderLayout.SOUTH);
        return bar;
    }

    private JScrollPane buildGridScroll() {
        gridPanel = new JPanel();
        gridPanel.setOpaque(false);
        gridPanel.setLayout(new GridLayout(0, 3, 18, 18));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(gridPanel, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    public void refreshData() {
        loadCategories();
        loadItems();
    }

    private void loadCategories() {
        CategoryItem current = (CategoryItem) kategoriCombo.getSelectedItem();
        String prevId = current != null ? current.id : null;

        // Hapus listener sementara agar tidak memicu loadItems berulang
        var listeners = kategoriCombo.getActionListeners();
        for (var l : listeners) kategoriCombo.removeActionListener(l);

        kategoriCombo.removeAllItems();
        kategoriCombo.addItem(new CategoryItem(null, "Semua Kategori"));
        try {
            List<Category> cats = controller.getAllCategories();
            for (Category c : cats) {
                CategoryItem item = new CategoryItem(c.getId(), c.getName());
                kategoriCombo.addItem(item);
                if (prevId != null && prevId.equals(c.getId())) {
                    kategoriCombo.setSelectedItem(item);
                }
            }
        } catch (ServiceException ignored) {
        }

        for (var l : listeners) kategoriCombo.addActionListener(l);
    }

    private void loadItems() {
        String keyword = searchField.getText();
        ItemType type = switch (tipeCombo.getSelectedIndex()) {
            case 1 -> ItemType.LOST;
            case 2 -> ItemType.FOUND;
            default -> null;
        };
        CategoryItem selectedCat = (CategoryItem) kategoriCombo.getSelectedItem();
        String catId = selectedCat != null ? selectedCat.id : null;

        gridPanel.removeAll();
        try {
            List<Item> items = controller.searchPublishedItems(keyword, catId, type);
            countLabel.setText("Menampilkan " + items.size() + " barang terbit");

            if (items.isEmpty()) {
                gridPanel.setLayout(new BorderLayout());
                RoundedPanel empty = new RoundedPanel(ThemeConfig.CARD_RADIUS, ThemeConfig.BORDER_CARD, 1, false);
                empty.setBorder(new EmptyBorder(48, 24, 48, 24));
                empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));

                JLabel t = new JLabel("Tidak Ada Barang Ditemukan");
                t.setFont(ThemeConfig.FONT_TITLE);
                t.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);
                t.setAlignmentX(Component.CENTER_ALIGNMENT);

                JLabel d = new JLabel("Coba ubah kata kunci pencarian atau filter kategori/tipe.");
                d.setFont(ThemeConfig.FONT_MEDIUM);
                d.setForeground(ThemeConfig.COLOR_TEXT_MUTED);
                d.setAlignmentX(Component.CENTER_ALIGNMENT);

                empty.add(t);
                empty.add(Box.createVerticalStrut(6));
                empty.add(d);
                gridPanel.add(empty, BorderLayout.CENTER);
            } else {
                gridPanel.setLayout(new GridLayout(0, 3, 18, 18));
                for (Item it : items) {
                    gridPanel.add(buildItemCard(it));
                }
            }
        } catch (ServiceException ex) {
            countLabel.setText("Gagal memuat katalog: " + ex.getMessage());
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private RoundedPanel buildItemCard(Item it) {
        RoundedPanel card = new RoundedPanel(ThemeConfig.CARD_RADIUS, ThemeConfig.BORDER_CARD, 1, true);
        card.setLayout(new BorderLayout());

        // Gambar di atas
        JLabel imgLbl = new JLabel(ImageHelper.loadScaledIcon(it.getPhoto(), 300, 180));
        imgLbl.setPreferredSize(new Dimension(0, 180));
        imgLbl.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(imgLbl, BorderLayout.NORTH);

        // Body
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(14, 16, 16, 16));

        // Meta badges
        JPanel meta = new JPanel(new BorderLayout(8, 0));
        meta.setOpaque(false);
        meta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        meta.setAlignmentX(Component.LEFT_ALIGNMENT);
        meta.add(StatusBadge.forKategori(it.getCategoryName() != null ? it.getCategoryName() : "Umum"), BorderLayout.WEST);
        meta.add(StatusBadge.forType(it.getType().getDb()), BorderLayout.EAST);

        JLabel title = new JLabel(it.getTitle());
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel loc = new JLabel("📍 " + it.getLocation());
        loc.setFont(ThemeConfig.FONT_SMALL);
        loc.setForeground(ThemeConfig.COLOR_TEXT_MUTED);
        loc.setAlignmentX(Component.LEFT_ALIGNMENT);

        String dateStr = it.getEventDate() != null ? it.getEventDate().format(DATE_FMT) : "-";
        JLabel date = new JLabel("📅 " + dateStr);
        date.setFont(ThemeConfig.FONT_SMALL);
        date.setForeground(ThemeConfig.COLOR_TEXT_MUTED);
        date.setAlignmentX(Component.LEFT_ALIGNMENT);

        body.add(meta);
        body.add(Box.createVerticalStrut(10));
        body.add(title);
        body.add(Box.createVerticalStrut(8));
        body.add(loc);
        body.add(Box.createVerticalStrut(4));
        body.add(date);
        body.add(Box.createVerticalStrut(14));

        // Tombol aksi (.card-actions)
        JPanel actions = new JPanel(new GridLayout(1, it.getType() == ItemType.FOUND ? 2 : 1, 8, 0));
        actions.setOpaque(false);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton detailBtn = RoundedButton.createOutlineButton("Detail");
        detailBtn.setPreferredSize(new Dimension(0, 38));
        detailBtn.addActionListener(e -> showDetailDialog(it));
        actions.add(detailBtn);

        if (it.getType() == ItemType.FOUND) {
            RoundedButton klaimBtn = new RoundedButton("Klaim");
            klaimBtn.setPreferredSize(new Dimension(0, 38));
            klaimBtn.addActionListener(e -> {
                if (onClaimRequested != null) onClaimRequested.accept(it.getId());
            });
            actions.add(klaimBtn);
        }

        body.add(actions);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private void showDetailDialog(Item it) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Detail Barang - " + it.getTitle(), Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(520, 600);
        dialog.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel img = new JLabel(ImageHelper.loadScaledIcon(it.getPhoto(), 460, 220));
        img.setHorizontalAlignment(SwingConstants.CENTER);
        root.add(img, BorderLayout.NORTH);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        badgeRow.setOpaque(false);
        badgeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        badgeRow.add(StatusBadge.forType(it.getType().getDb()));
        badgeRow.add(StatusBadge.forKategori(it.getCategoryName() != null ? it.getCategoryName() : "Umum"));
        badgeRow.add(StatusBadge.forItemStatus(it.getStatus().getDb()));

        JLabel titleLbl = new JLabel(it.getTitle());
        titleLbl.setFont(ThemeConfig.FONT_TITLE);
        titleLbl.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea desc = new JTextArea(it.getDescription());
        desc.setEditable(false);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setFont(ThemeConfig.FONT_MEDIUM);
        desc.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);
        desc.setBackground(ThemeConfig.BG_PAGE);
        desc.setBorder(new EmptyBorder(10, 12, 10, 12));
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        String contactInfo = it.getType() == ItemType.FOUND
                ? "Penemu: " + (it.getFinderName() != null ? it.getFinderName() : "-")
                + (it.getFinderPhone() != null ? " (" + it.getFinderPhone() + ")" : "")
                : "Pelapor: " + (it.getReporterName() != null ? it.getReporterName() : "-");

        JLabel metaLbl = new JLabel("<html><b>Lokasi:</b> " + it.getLocation()
                + "<br><b>Tanggal Kejadian:</b> " + (it.getEventDate() != null ? it.getEventDate().format(DATE_FMT) : "-")
                + "<br><b>" + contactInfo + "</b></html>");
        metaLbl.setFont(ThemeConfig.FONT_REGULAR);
        metaLbl.setForeground(ThemeConfig.COLOR_TEXT_MUTED);
        metaLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(badgeRow);
        info.add(Box.createVerticalStrut(10));
        info.add(titleLbl);
        info.add(Box.createVerticalStrut(10));
        info.add(desc);
        info.add(Box.createVerticalStrut(12));
        info.add(metaLbl);

        root.add(info, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        RoundedButton closeBtn = RoundedButton.createOutlineButton("Tutup");
        closeBtn.setPreferredSize(new Dimension(100, 38));
        closeBtn.addActionListener(e -> dialog.dispose());
        footer.add(closeBtn);

        if (it.getType() == ItemType.FOUND) {
            RoundedButton claimBtn = new RoundedButton("Klaim Barang Ini");
            claimBtn.setPreferredSize(new Dimension(155, 38));
            claimBtn.addActionListener(e -> {
                dialog.dispose();
                if (onClaimRequested != null) onClaimRequested.accept(it.getId());
            });
            footer.add(claimBtn);
        }

        root.add(footer, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private static class CategoryItem {
        final String id;
        final String name;
        CategoryItem(String id, String name) {
            this.id = id;
            this.name = name;
        }
        @Override public String toString() { return name; }
    }
}
