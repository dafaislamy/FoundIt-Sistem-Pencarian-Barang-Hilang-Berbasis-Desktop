package com.foundit.apk.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Form edit profil: nama, nomor identitas, email, telepon seperti edit-profil.php. */
public class EditProfilFrame extends JFrame {
    private final String loginEmail, role;
    private JTextField nama, nim, email, telp;

    public EditProfilFrame(String email, String role) {
        this.loginEmail = email;
        this.role = role;
        UiKit.frame(this, "FoundIt | Edit Profil", 1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setContentPane(build());
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiKit.BG);

        // Sidebar kiri tetap tampil, menu "Profil" ditandai aktif
        root.add(UserSidebar.create(this, loginEmail, role, "Profil"), BorderLayout.WEST);

        JPanel form = UiKit.card();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(28, 34, 28, 34));

        JPanel header = UiKit.header("Edit Profil", "Perbarui informasi akun kamu.");
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(header);
        form.add(Box.createVerticalStrut(24));

        nama = UiKit.field();
        nama.setText(ProfileStore.get(loginEmail, "nama", "Pengguna FoundIt"));
        nim = UiKit.field();
        nim.setText(ProfileStore.get(loginEmail, "nim", ""));
        email = UiKit.field();
        email.setText(ProfileStore.get(loginEmail, "email", loginEmail));
        telp = UiKit.field();
        telp.setText(ProfileStore.get(loginEmail, "telp", ""));

        addField(form, "Nama Lengkap", nama);
        addField(form, "NIM / Nomor Identitas", nim);
        addField(form, "Email", email);
        addField(form, "Nomor WhatsApp", telp);
        form.add(Box.createVerticalStrut(10));

        // Tombol dengan ukuran tetap supaya teks tidak terpotong / gepeng
        JButton cancel = UiKit.button("Kembali", UiKit.PALE, UiKit.BLUE_DARK);
        sizeButton(cancel, 130, 44);
        cancel.addActionListener(e -> openProfile());

        JButton save = UiKit.button("Simpan Perubahan", UiKit.BLUE, Color.WHITE);
        sizeButton(save, 190, 44);
        save.addActionListener(e -> save());

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        buttons.add(cancel);
        buttons.add(Box.createHorizontalStrut(12));
        buttons.add(save);
        form.add(buttons);

        // Wrapper agar kartu tidak melar ke bawah
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(form, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(26, 34, 25, 34));
        main.add(scroll, BorderLayout.CENTER);

        root.add(main, BorderLayout.CENTER);
        return root;
    }

    private void addField(JPanel form, String title, JTextField input) {
        JPanel block = UiKit.fieldBlock(title, input);
        block.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(block);
        form.add(Box.createVerticalStrut(14));
    }

    private void sizeButton(JButton b, int w, int h) {
        b.setBorder(new EmptyBorder(8, 24, 8, 24));
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setVerticalAlignment(SwingConstants.CENTER);
        b.setPreferredSize(new Dimension(w, h));
        b.setMinimumSize(new Dimension(w, h));
        b.setMaximumSize(new Dimension(w, h));
    }

    private void save() {
        String n = nama.getText().trim();
        String em = email.getText().trim();
        String id = nim.getText().trim();
        String hp = telp.getText().trim();

        if (n.isEmpty() || n.length() > 100) {
            UiKit.info(this, "Nama wajib diisi (maksimal 100 karakter).");
            return;
        }
        if (!em.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            UiKit.info(this, "Format email tidak valid.");
            return;
        }
        if (!id.isEmpty() && !id.matches("[A-Za-z0-9]{3,20}")) {
            UiKit.info(this, "NIM harus 3-20 karakter huruf/angka.");
            return;
        }
        if (!hp.isEmpty() && !hp.matches("[0-9+\\-\\s]{8,20}")) {
            UiKit.info(this, "Nomor WhatsApp tidak valid.");
            return;
        }

        ProfileStore.set(loginEmail, "nama", n);
        ProfileStore.set(loginEmail, "nim", id);
        ProfileStore.set(loginEmail, "email", em);
        ProfileStore.set(loginEmail, "telp", hp);

        UiKit.info(this, "Profil berhasil diperbarui.");
        openProfile();
    }

    private void openProfile() {
        new ProfilFrame(loginEmail, role).setVisible(true);
        dispose();
    }
}
