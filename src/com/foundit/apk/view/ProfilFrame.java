package com.foundit.apk.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Halaman profil pengguna seperti profil.php. */
public class ProfilFrame extends JFrame {
    private final String email, role;

    public ProfilFrame(String email, String role) {
        this.email = email;
        this.role = role;
        UiKit.frame(this, "FoundIt | Profil", 1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setContentPane(build());
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiKit.BG);

        // Sidebar kiri, menu "Profil" ditandai aktif
        root.add(UserSidebar.create(this, email, role, "Profil"), BorderLayout.WEST);

        JPanel content = UiKit.card();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(30, 34, 30, 34));

        String nama = ProfileStore.get(email, "nama", "Pengguna FoundIt");

        JPanel header = UiKit.header("Profil Saya", "Kelola informasi akun kamu.");
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(header);
        content.add(Box.createVerticalStrut(24));

        JLabel avatar = UiKit.label(nama.substring(0, 1).toUpperCase(), 24, true, Color.WHITE);
        avatar.setOpaque(true);
        avatar.setBackground(UiKit.BLUE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(64, 64));
        avatar.setMaximumSize(new Dimension(64, 64));
        avatar.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(avatar);
        content.add(Box.createVerticalStrut(10));

        addLabel(content, UiKit.label(nama, 20, true, UiKit.TEXT));
        addLabel(content, UiKit.label(ProfileStore.get(email, "nim", "NIM belum diisi"), 12, false, UiKit.MUTED));
        content.add(Box.createVerticalStrut(25));

        info(content, "Nama Lengkap", nama);
        info(content, "NIM / Nomor Identitas", ProfileStore.get(email, "nim", "-"));
        info(content, "Email", ProfileStore.get(email, "email", email));
        info(content, "Nomor WhatsApp", ProfileStore.get(email, "telp", "-"));
        content.add(Box.createVerticalStrut(18));

        JButton edit = UiKit.button("Edit Profil", UiKit.BLUE, Color.WHITE);
        edit.setBorder(new EmptyBorder(8, 24, 8, 24));
        edit.setPreferredSize(new Dimension(150, 44));
        edit.setMinimumSize(new Dimension(150, 44));
        edit.setMaximumSize(new Dimension(150, 44));
        edit.setAlignmentX(Component.LEFT_ALIGNMENT);
        edit.addActionListener(e -> {
            new EditProfilFrame(email, role).setVisible(true);
            dispose();
        });
        content.add(edit);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(content, BorderLayout.NORTH);

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

    private void addLabel(JPanel p, JLabel l) {
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
    }

    private void info(JPanel p, String label, String value) {
        addLabel(p, UiKit.label(label, 12, true, UiKit.MUTED));
        p.add(Box.createVerticalStrut(3));
        addLabel(p, UiKit.label(value.isEmpty() ? "-" : value, 15, false, UiKit.TEXT));
        p.add(Box.createVerticalStrut(12));
    }
}
