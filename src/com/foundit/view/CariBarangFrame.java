package com.foundit.view;

import com.foundit.model.Category;
import com.foundit.model.Item;
import com.foundit.model.ItemType;
import com.foundit.service.CategoryService;
import com.foundit.service.ItemService;
import com.foundit.service.ServiceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;

/**
 * Halaman Cari Barang terhubung langsung ke ItemService & CategoryService (Database)[cite: 47].
 */
public class CariBarangFrame extends JFrame {

    private final ItemService itemService = new ItemService();
    private final CategoryService categoryService = new CategoryService();

    private JTextField tfSearch;
    private JButton btnClearSearch;
    private JComboBox<String> cbStatus, cbKategori;
    private JLabel lblResultCount;
    private JPanel gridWrapper;

    public CariBarangFrame() {
        UiKit.frame(this, "FoundIt | Cari Barang", 1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setContentPane(build());
        loadCategories();
        bindEvents();
        performFilter();
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiKit.BG);
        root.add(new UserSidebarPanel(this, UserSidebarPanel.Menu.CARI_BARANG), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(26, 34, 25, 34));
        main.add(UserSidebarPanel.buildTopbar("Daftar Barang", "Jelajahi laporan barang hilang dan ditemukan di kampus"), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(22, 0, 10, 8));

        JPanel searchBox = UiKit.card();
        searchBox.setBackground(Color.WHITE);
        searchBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(237, 240, 242)),
                new EmptyBorder(24, 26, 24, 26)
        ));
        searchBox.setLayout(new BorderLayout(0, 18));
        searchBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 155));

        JPanel searchTitle = new JPanel();
        searchTitle.setOpaque(false);
        searchTitle.setLayout(new BoxLayout(searchTitle, BoxLayout.Y_AXIS));
        searchTitle.add(UiKit.label("Cari Barang", 20, true, UiKit.TEXT));
        searchTitle.add(Box.createVerticalStrut(5));
        searchTitle.add(UiKit.label("Temukan barang berdasarkan nama, status, atau kategori.", 13, false, UiKit.MUTED));
        searchBox.add(searchTitle, BorderLayout.NORTH);

        JPanel filterRow = new JPanel(new BorderLayout(12, 0));
        filterRow.setOpaque(false);

        JPanel inputWrapper = new JPanel(new BorderLayout(6, 0));
        inputWrapper.setBackground(new Color(248, 250, 252));
        inputWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(223, 230, 233)),
                new EmptyBorder(0, 12, 0, 8)
        ));
        inputWrapper.setPreferredSize(new Dimension(320, 44));

        JLabel searchIcon = new JLabel("🔍");
        inputWrapper.add(searchIcon, BorderLayout.WEST);

        tfSearch = new JTextField();
        tfSearch.setFont(UiKit.REG);
        tfSearch.setBorder(null);
        tfSearch.setOpaque(false);
        inputWrapper.add(tfSearch, BorderLayout.CENTER);

        btnClearSearch = new JButton("✕");
        btnClearSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClearSearch.setForeground(new Color(127, 140, 141));
        btnClearSearch.setBorderPainted(false);
        btnClearSearch.setContentAreaFilled(false);
        btnClearSearch.setFocusPainted(false);
        btnClearSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClearSearch.setVisible(false);
        inputWrapper.add(btnClearSearch, BorderLayout.EAST);

        filterRow.add(inputWrapper, BorderLayout.CENTER);

        JPanel controlsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlsRight.setOpaque(false);

        cbStatus = new JComboBox<>(new String[]{"Semua Status", "Hilang", "Ditemukan"});
        cbStatus.setFont(UiKit.REG);
        cbStatus.setBackground(Color.WHITE);
        cbStatus.setPreferredSize(new Dimension(155, 44));

        cbKategori = new JComboBox<>(new String[]{"Semua Kategori"});
        cbKategori.setFont(UiKit.REG);
        cbKategori.setBackground(Color.WHITE);
        cbKategori.setPreferredSize(new Dimension(165, 44));

        JButton btnReset = UiKit.button("Reset Filter", new Color(248, 250, 252), new Color(71, 85, 105));
        btnReset.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnReset.setPreferredSize(new Dimension(125, 44));
        btnReset.addActionListener(e -> {
            tfSearch.setText("");
            cbStatus.setSelectedIndex(0);
            cbKategori.setSelectedIndex(0);
            performFilter();
            tfSearch.requestFocus();
        });

        controlsRight.add(cbStatus);
        controlsRight.add(cbKategori);
        controlsRight.add(btnReset);
        filterRow.add(controlsRight, BorderLayout.EAST);

        searchBox.add(filterRow, BorderLayout.CENTER);
        body.add(searchBox);
        body.add(Box.createVerticalStrut(24));

        JPanel resultHeader = new JPanel(new BorderLayout());
        resultHeader.setOpaque(false);
        resultHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        JPanel rhLeft = new JPanel();
        rhLeft.setOpaque(false);
        rhLeft.setLayout(new BoxLayout(rhLeft, BoxLayout.Y_AXIS));
        rhLeft.add(UiKit.label("Daftar Barang", 20, true, UiKit.TEXT));
        rhLeft.add(Box.createVerticalStrut(4));
        lblResultCount = UiKit.label("Menampilkan semua barang", 13, false, UiKit.MUTED);
        rhLeft.add(lblResultCount);
        resultHeader.add(rhLeft, BorderLayout.WEST);

        body.add(resultHeader);
        body.add(Box.createVerticalStrut(14));

        gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        body.add(gridWrapper);

        JScrollPane sc = new JScrollPane(body);
        sc.setBorder(null);
        sc.getViewport().setOpaque(false);
        sc.setOpaque(false);
        sc.getVerticalScrollBar().setUnitIncrement(18);

        main.add(sc, BorderLayout.CENTER);
        root.add(main, BorderLayout.CENTER);
        return root;
    }

    private void loadCategories() {
        try {
            cbKategori.removeAllItems();
            cbKategori.addItem("Semua Kategori");
            for (Category c : categoryService.findAll()) {
                cbKategori.addItem(c.getName());
            }
        } catch (ServiceException ignored) {
        }
    }

    private void bindEvents() {
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { onSearchChanged(); }
            @Override
            public void removeUpdate(DocumentEvent e) { onSearchChanged(); }
            @Override
            public void changedUpdate(DocumentEvent e) { onSearchChanged(); }
        });
        btnClearSearch.addActionListener(e -> {
            tfSearch.setText("");
            tfSearch.requestFocus();
        });
        cbStatus.addActionListener(e -> performFilter());
        cbKategori.addActionListener(e -> performFilter());
    }

    private void onSearchChanged() {
        btnClearSearch.setVisible(!tfSearch.getText().trim().isEmpty());
        performFilter();
    }

    private void performFilter() {
        String kw = tfSearch.getText().trim().toLowerCase();
        String st = (String) cbStatus.getSelectedItem();
        String kat = (String) cbKategori.getSelectedItem();

        try {
            List<Item> filtered = itemService.findAll().stream().filter(item -> {
                boolean matchKw = kw.isEmpty()
                        || (item.getTitle() != null && item.getTitle().toLowerCase().contains(kw))
                        || (item.getDescription() != null && item.getDescription().toLowerCase().contains(kw))
                        || (item.getLocation() != null && item.getLocation().toLowerCase().contains(kw));

                boolean matchSt = st == null || st.contains("Semua")
                        || ("Hilang".equalsIgnoreCase(st) && item.getType() == ItemType.LOST)
                        || ("Ditemukan".equalsIgnoreCase(st) && item.getType() == ItemType.FOUND);

                String itemCat = item.getCategoryName() != null ? item.getCategoryName() : "";
                boolean matchKat = kat == null || kat.contains("Semua") || itemCat.equalsIgnoreCase(kat);

                return matchKw && matchSt && matchKat;
            }).toList();

            renderGrid(filtered);
        } catch (ServiceException ex) {
            UiKit.error(this, ex.getMessage());
        }
    }

    private void renderGrid(List<Item> hasil) {
        gridWrapper.removeAll();
        if (hasil.isEmpty()) {
            lblResultCount.setText("Tidak ada barang yang sesuai");
            JPanel noResultPanel = UiKit.card();
            noResultPanel.setBackground(Color.WHITE);
            noResultPanel.setBorder(new EmptyBorder(50, 20, 50, 20));
            noResultPanel.setLayout(new BoxLayout(noResultPanel, BoxLayout.Y_AXIS));

            JLabel icon = UiKit.label("🔎", 42, false, UiKit.TEXT);
            icon.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel title = UiKit.label("Barang tidak ditemukan", 19, true, UiKit.TEXT);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel sub = UiKit.label("Coba gunakan kata kunci atau filter yang berbeda.", 13, false, UiKit.MUTED);
            sub.setAlignmentX(Component.CENTER_ALIGNMENT);

            noResultPanel.add(icon);
            noResultPanel.add(Box.createVerticalStrut(12));
            noResultPanel.add(title);
            noResultPanel.add(Box.createVerticalStrut(6));
            noResultPanel.add(sub);
            gridWrapper.add(noResultPanel, BorderLayout.NORTH);
        } else {
            lblResultCount.setText("Menampilkan " + hasil.size() + " barang");
            int cols = 2;
            int rows = (int) Math.ceil(hasil.size() / (double) cols);
            JPanel grid = new JPanel(new GridLayout(rows, cols, 18, 18));
            grid.setOpaque(false);

            for (Item item : hasil) {
                grid.add(createBarangCard(item));
            }
            if (hasil.size() % cols != 0) {
                JPanel dummy = new JPanel();
                dummy.setOpaque(false);
                grid.add(dummy);
            }
            gridWrapper.add(grid, BorderLayout.NORTH);
        }
        gridWrapper.revalidate();
        gridWrapper.repaint();
    }

    private JPanel createBarangCard(Item item) {
        boolean isHilang = item.getType() == ItemType.LOST;
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(237, 240, 242), 1));

        JPanel imgBox = new JPanel(new BorderLayout());
        imgBox.setBackground(new Color(241, 243, 245));
        imgBox.setPreferredSize(new Dimension(0, 120));

        JLabel lblEmoji = new JLabel(isHilang ? "🔍" : "📦", SwingConstants.CENTER);
        lblEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        imgBox.add(lblEmoji, BorderLayout.CENTER);
        card.add(imgBox, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(18, 20, 18, 20));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel statusBadge = new JLabel(isHilang ? "Hilang" : "Ditemukan");
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusBadge.setOpaque(true);
        statusBadge.setBorder(new EmptyBorder(4, 10, 4, 10));
        if (isHilang) {
            statusBadge.setBackground(new Color(253, 236, 234));
            statusBadge.setForeground(new Color(231, 76, 60));
        } else {
            statusBadge.setBackground(new Color(234, 250, 241));
            statusBadge.setForeground(new Color(39, 174, 96));
        }
        statusBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNama = UiKit.label(item.getTitle(), 17, true, UiKit.TEXT);
        lblNama.setAlignmentX(Component.LEFT_ALIGNMENT);

        String descText = (item.getLocation() != null ? item.getLocation() + " - " : "")
                + (item.getDescription() != null ? item.getDescription() : "");
        JLabel lblDesc = UiKit.label(descText, 13, false, UiKit.MUTED);
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        String katText = item.getCategoryName() != null ? item.getCategoryName() : "Umum";
        JLabel catBadge = new JLabel(katText);
        catBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        catBadge.setOpaque(true);
        catBadge.setBackground(new Color(241, 247, 252));
        catBadge.setForeground(UiKit.BLUE);
        catBadge.setBorder(new EmptyBorder(4, 9, 4, 9));
        catBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(statusBadge);
        content.add(Box.createVerticalStrut(10));
        content.add(lblNama);
        content.add(Box.createVerticalStrut(6));
        content.add(lblDesc);
        content.add(Box.createVerticalStrut(12));
        content.add(catBadge);
        content.add(Box.createVerticalStrut(16));

        JPanel actions = new JPanel(new GridLayout(1, isHilang ? 1 : 2, 10, 0));
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton btnDetail = UiKit.button("Lihat Detail", UiKit.BLUE, Color.WHITE);
        btnDetail.setPreferredSize(new Dimension(0, 38));
        btnDetail.addActionListener(e -> UiKit.info(this,
                "Nama Barang: " + item.getTitle()
                        + "\nStatus: " + (isHilang ? "Hilang" : "Ditemukan")
                        + "\nKategori: " + katText
                        + "\nLokasi: " + (item.getLocation() != null ? item.getLocation() : "-")
                        + "\nDeskripsi: " + (item.getDescription() != null ? item.getDescription() : "-")
        ));
        actions.add(btnDetail);

        if (!isHilang) {
            JButton btnKlaim = UiKit.button("Klaim", new Color(39, 174, 96), Color.WHITE);
            btnKlaim.setPreferredSize(new Dimension(0, 38));
            btnKlaim.addActionListener(e -> {
                dispose();
                new KlaimFrame(item.getId(), item.getTitle()).setVisible(true);
            });
            actions.add(btnKlaim);
        }

        content.add(actions);
        card.add(content, BorderLayout.CENTER);
        return card;
    }
}