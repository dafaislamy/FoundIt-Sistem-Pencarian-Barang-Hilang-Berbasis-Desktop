package com.foundit.apk.view;

import com.foundit.apk.controller.ReportController;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Form laporan dasar dengan sidebar navigasi 8 menu (View murni).
 */
public abstract class ReportFormFrame extends JFrame {

    private final String email, role, type, title;
    private JTextField name, location, date, photo;
    private JComboBox<String> category;
    private JTextArea description;
    private JButton choose, reset, cancel, submit, back;
    private final Map<String, Runnable> menuActions = new HashMap<>();

    public ReportFormFrame(String email, String role, String type, String title) {
        this(email, role, type, title, true);
    }

    public ReportFormFrame(String email, String role, String type, String title, boolean bindDefaultController) {
        this.email = email;
        this.role = role;
        this.type = type;
        this.title = title;

        UiKit.frame(this, "FoundIt | " + title, 1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setContentPane(build());

        if (bindDefaultController) {
            new ReportController(this, email, role, type, title).bind();
        }
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiKit.BG);

        // SIDEBAR LENGKAP 8 MENU
        JPanel side = new JPanel();
        side.setBackground(Color.WHITE);
        side.setPreferredSize(new Dimension(238, 0));
        side.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UiKit.BORDER));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));

        JPanel logo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logo.setOpaque(false);
        logo.setBorder(new EmptyBorder(27, 25, 32, 0));

        logo.add(UiKit.label("Found", 27, true, UiKit.BLUE_DARK));
        logo.add(UiKit.label("It", 27, true, UiKit.BLUE));

        side.add(logo);
        side.add(menu("Dashboard", "home", false, () -> triggerMenu("Dashboard")));
        side.add(menu("Cari Barang", "search", false, () -> triggerMenu("Cari Barang")));
        side.add(menu(
            "Lapor Barang Hilang",
            "lost",
            type.equals("HILANG"),
            () -> triggerMenu("Lapor Barang Hilang")
        ));
        side.add(menu(
            "Lapor Barang Ditemukan",
            "found",
            type.equals("DITEMUKAN"),
            () -> triggerMenu("Lapor Barang Ditemukan")
        ));
        side.add(menu("Klaim Barang", "claim", false, () -> triggerMenu("Klaim Barang")));
        side.add(menu("Status Klaim", "status", false, () -> triggerMenu("Status Klaim")));
        side.add(menu("Riwayat Laporan", "history", false, () -> triggerMenu("Riwayat Laporan")));
        side.add(menu("Profil", "profile", false, () -> triggerMenu("Profil")));

        side.add(Box.createVerticalGlue());

        side.add(menu("Keluar", "return", false, () -> triggerMenu("Keluar")));
        side.add(Box.createVerticalStrut(25));
        root.add(side, BorderLayout.WEST);

        // AREA KANAN
        JPanel rightArea = new JPanel(new BorderLayout());
        rightArea.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.setBorder(new EmptyBorder(16, 30, 16, 30));

        back = UiKit.button("Kembali ke Dashboard", UiKit.PALE, UiKit.BLUE_DARK);
        top.add(back, BorderLayout.WEST);
        top.add(UiKit.label("FoundIt", 23, true, UiKit.BLUE), BorderLayout.EAST);

        rightArea.add(top, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(28, 42, 30, 42));

        JPanel heading = UiKit.header(
            title,
            "Lengkapi informasi berikut dengan jelas agar laporan mudah dipahami."
        );
        content.add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(20, 0, 20, 0));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 12, 10, 12);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;

        name = UiKit.field();
        category = new JComboBox<>(new String[]{"Elektronik", "Aksesoris", "Dokumen", "Kunci", "Lainnya"});
        category.setFont(UiKit.REG);
        category.setBackground(Color.WHITE);

        form.add(UiKit.fieldBlock("Nama Barang *", name), c);

        c.gridx = 1;
        form.add(UiKit.fieldBlock("Kategori *", category), c);

        c.gridx = 0;
        c.gridy = 1;

        location = UiKit.field();
        date = UiKit.field();
        date.setText(LocalDate.now().toString());

        form.add(
            UiKit.fieldBlock(
                type.equals("HILANG") ? "Lokasi Terakhir *" : "Lokasi Ditemukan *",
                location
            ),
            c
        );

        c.gridx = 1;
        form.add(UiKit.fieldBlock("Tanggal (YYYY-MM-DD) *", date), c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;

        description = new JTextArea(7, 30);
        description.setFont(UiKit.REG);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setBorder(new EmptyBorder(10, 12, 10, 12));

        JScrollPane descScroll = new JScrollPane(description);
        descScroll.setPreferredSize(new Dimension(500, 150));
        descScroll.setBorder(BorderFactory.createLineBorder(new Color(190, 207, 222), 1, true));

        form.add(UiKit.fieldBlock("Deskripsi Barang *", descScroll), c);

        c.gridy = 3;

        photo = UiKit.field();
        photo.setEditable(false);

        choose = UiKit.button("Pilih Foto", UiKit.PALE, UiKit.BLUE_DARK);

        JPanel photoRow = new JPanel(new BorderLayout(10, 0));
        photoRow.setOpaque(false);
        photoRow.add(photo, BorderLayout.CENTER);
        photoRow.add(choose, BorderLayout.EAST);

        form.add(UiKit.fieldBlock("Foto Barang (opsional)", photoRow), c);

        content.add(form, BorderLayout.CENTER);

        reset = UiKit.button("Reset Form", new Color(255, 238, 236), new Color(190, 70, 60));
        cancel = UiKit.button("Batal", new Color(237, 241, 245), UiKit.TEXT);
        submit = UiKit.button("Kirim Laporan", UiKit.BLUE, Color.WHITE);

        JScrollPane sc = new JScrollPane(content);
        sc.setBorder(null);
        sc.getVerticalScrollBar().setUnitIncrement(18);

        rightArea.add(sc, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UiKit.BORDER));

        footer.add(reset);
        footer.add(cancel);
        footer.add(submit);

        rightArea.add(footer, BorderLayout.SOUTH);
        root.add(rightArea, BorderLayout.CENTER);

        return root;
    }

    private JPanel menu(String text, String icon, boolean active, Runnable action) {
        JPanel p = new JPanel(new BorderLayout());
        p.setMaximumSize(new Dimension(220, 44));
        p.setBorder(new EmptyBorder(10, 18, 10, 10));
        p.setBackground(active ? UiKit.PALE : Color.WHITE);

        JLabel item = new JLabel(text);
        item.setHorizontalAlignment(SwingConstants.LEFT);
        item.setFont(new Font("Segoe UI", Font.BOLD, 13));
        item.setForeground(active ? UiKit.BLUE : UiKit.MUTED);

        p.add(item);
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
        });

        return p;
    }

    private void triggerMenu(String menuName) {
        Runnable r = menuActions.get(menuName);
        if (r != null) {
            r.run();
        }
    }

    public void setMenuAction(String menuName, Runnable action) {
        menuActions.put(menuName, action);
    }

    public void resetFormFields() {
        name.setText("");
        category.setSelectedIndex(0);
        location.setText("");
        date.setText(LocalDate.now().toString());
        description.setText("");
        photo.setText("");
        name.requestFocus();
    }

    public String getNameInput() { return name.getText().trim(); }
    public String getCategoryInput() { return (String) category.getSelectedItem(); }
    public String getLocationInput() { return location.getText().trim(); }
    public String getDateInput() { return date.getText().trim(); }
    public String getDescriptionInput() { return description.getText().trim(); }
    public String getPhotoInput() { return photo.getText(); }
    public void setPhotoInput(String path) { photo.setText(path); }
    public void focusName() { name.requestFocus(); }

    public void addChoosePhotoListener(ActionListener l) { choose.addActionListener(l); }
    public void addResetListener(ActionListener l) { reset.addActionListener(l); }
    public void addCancelListener(ActionListener l) { cancel.addActionListener(l); }
    public void addSubmitListener(ActionListener l) { submit.addActionListener(l); }
    public void addBackListener(ActionListener l) { back.addActionListener(l); }
}
