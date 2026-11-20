package com.foundit.view;

import com.foundit.model.Category;
import com.foundit.model.Item;
import com.foundit.model.ItemStatus;
import com.foundit.model.ItemType;
import com.foundit.service.CategoryService;
import com.foundit.service.ItemService;
import com.foundit.service.ServiceException;
import com.foundit.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Form laporan barang hilang/ditemukan terhubung ke ItemService & CategoryService (Database)[cite: 55].
 */
public abstract class ReportFormFrame extends JFrame {

    private final ItemService itemService = new ItemService();
    private final CategoryService categoryService = new CategoryService();

    private final ItemType itemType;
    private final String title;

    private JTextField nameField, locationField, dateField, photoField;
    private JComboBox<Category> categoryCombo;
    private JTextArea descriptionArea;

    protected ReportFormFrame(ItemType itemType, String title) {
        this.itemType = itemType;
        this.title = title;

        UiKit.frame(this, "FoundIt | " + title, 1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setContentPane(build());
        loadCategories();
    }

    private JPanel build() {
        UserSidebarPanel.Menu activeMenu = itemType == ItemType.LOST
                ? UserSidebarPanel.Menu.LAPOR_HILANG
                : UserSidebarPanel.Menu.LAPOR_DITEMUKAN;

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiKit.BG);
        root.add(new UserSidebarPanel(this, activeMenu), BorderLayout.WEST);

        JPanel rightArea = new JPanel(new BorderLayout());
        rightArea.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.setBorder(new EmptyBorder(16, 30, 16, 30));

        JButton backBtn = UiKit.button("Kembali ke Dashboard", UiKit.PALE, UiKit.BLUE_DARK);
        backBtn.setPreferredSize(new Dimension(190, 38));
        backBtn.addActionListener(e -> {
            dispose();
            new UserDashboardFrame().setVisible(true);
        });
        top.add(backBtn, BorderLayout.WEST);
        top.add(UiKit.label("FoundIt", 23, true, UiKit.BLUE), BorderLayout.EAST);
        rightArea.add(top, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(28, 42, 30, 42));
        content.add(UiKit.header(title, "Lengkapi informasi berikut dengan jelas agar laporan mudah dipahami."), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(20, 0, 20, 0));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 12, 10, 12);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        nameField = UiKit.field();
        categoryCombo = new JComboBox<>();
        categoryCombo.setFont(UiKit.REG);
        categoryCombo.setBackground(Color.WHITE);

        c.gridx = 0; c.gridy = 0;
        form.add(UiKit.fieldBlock("Nama Barang *", nameField), c);
        c.gridx = 1;
        form.add(UiKit.fieldBlock("Kategori *", categoryCombo), c);

        locationField = UiKit.field();
        dateField = UiKit.field();
        dateField.setText(LocalDate.now().toString());

        c.gridx = 0; c.gridy = 1;
        form.add(UiKit.fieldBlock(itemType == ItemType.LOST ? "Lokasi Terakhir *" : "Lokasi Ditemukan *", locationField), c);
        c.gridx = 1;
        form.add(UiKit.fieldBlock("Tanggal (YYYY-MM-DD) *", dateField), c);

        descriptionArea = new JTextArea(7, 30);
        descriptionArea.setFont(UiKit.REG);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(new EmptyBorder(10, 12, 10, 12));
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(500, 150));
        descScroll.setBorder(BorderFactory.createLineBorder(new Color(190, 207, 222), 1, true));

        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        form.add(UiKit.fieldBlock("Deskripsi Barang *", descScroll), c);

        photoField = UiKit.field();
        photoField.setEditable(false);
        JButton chooseBtn = UiKit.button("Pilih Foto", UiKit.PALE, UiKit.BLUE_DARK);
        chooseBtn.setPreferredSize(new Dimension(120, 42));
        chooseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                photoField.setText(f.getAbsolutePath());
            }
        });

        JPanel photoRow = new JPanel(new BorderLayout(10, 0));
        photoRow.setOpaque(false);
        photoRow.add(photoField, BorderLayout.CENTER);
        photoRow.add(chooseBtn, BorderLayout.EAST);

        c.gridy = 3;
        form.add(UiKit.fieldBlock("Foto Barang (opsional)", photoRow), c);

        content.add(form, BorderLayout.CENTER);

        JScrollPane sc = new JScrollPane(content);
        sc.setBorder(null);
        sc.getVerticalScrollBar().setUnitIncrement(18);
        rightArea.add(sc, BorderLayout.CENTER);

        JButton resetBtn = UiKit.button("Reset Form", new Color(255, 238, 236), new Color(190, 70, 60));
        resetBtn.setPreferredSize(new Dimension(120, 40));
        resetBtn.addActionListener(e -> resetForm());

        JButton cancelBtn = UiKit.button("Batal", new Color(237, 241, 245), UiKit.TEXT);
        cancelBtn.setPreferredSize(new Dimension(100, 40));
        cancelBtn.addActionListener(e -> {
            dispose();
            new UserDashboardFrame().setVisible(true);
        });

        JButton submitBtn = UiKit.button("Kirim Laporan", UiKit.BLUE, Color.WHITE);
        submitBtn.setPreferredSize(new Dimension(145, 40));
        submitBtn.addActionListener(e -> handleSubmit());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UiKit.BORDER));
        footer.add(resetBtn);
        footer.add(cancelBtn);
        footer.add(submitBtn);
        rightArea.add(footer, BorderLayout.SOUTH);

        root.add(rightArea, BorderLayout.CENTER);
        return root;
    }

    private void loadCategories() {
        List<Category> list = new ArrayList<>();
        try {
            list = categoryService.findAll();
        } catch (ServiceException ignored) {
        }
        categoryCombo.removeAllItems();
        for (Category c : list) {
            categoryCombo.addItem(c);
        }
    }

    private void resetForm() {
        nameField.setText("");
        if (categoryCombo.getItemCount() > 0) categoryCombo.setSelectedIndex(0);
        locationField.setText("");
        dateField.setText(LocalDate.now().toString());
        descriptionArea.setText("");
        photoField.setText("");
        nameField.requestFocus();
    }

    private void handleSubmit() {
        if (Session.getUser() == null) {
            UiKit.warn(this, "Silakan login sebagai User terlebih dahulu.");
            return;
        }
        try {
            LocalDate d = LocalDate.parse(dateField.getText().trim());
            Category selectedCat = (Category) categoryCombo.getSelectedItem();

            Item item = new Item();
            item.setTitle(nameField.getText().trim());
            item.setDescription(descriptionArea.getText().trim());
            item.setType(itemType);
            item.setLocation(locationField.getText().trim());
            item.setIncidentAt(d.atStartOfDay());
            item.setPhotoPath(photoField.getText().trim().isEmpty() ? null : photoField.getText().trim());
            item.setStatus(ItemStatus.PENDING);
            item.setReportedByUserId(Session.getUser().getId());
            if (selectedCat != null) {
                item.setCategoryId(selectedCat.getId());
            }

            itemService.save(item);
            UiKit.info(this, "Laporan berhasil dikirim ke database (status: Menunggu Verifikasi).");
            dispose();
            new RiwayatLaporanFrame().setVisible(true);
        } catch (Exception ex) {
            UiKit.warn(this, ex.getMessage());
        }
    }
}