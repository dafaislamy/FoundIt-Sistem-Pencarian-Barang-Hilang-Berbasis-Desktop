package com.foundit.apk.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Login dengan pemilihan peran berbentuk segmented cards.
 */
public class LoginFrame extends JFrame {

    private JTextField email;
    private JPasswordField password;
    private JLabel length, upper, symbol;
    private String role = "user";
    private JButton userChoice, adminChoice;

    public LoginFrame() {
        UiKit.frame(this, "FoundIt | Login", 1160, 740);
        setContentPane(build());
    }

    private JPanel build() {
        JPanel root = new JPanel(new GridLayout(1, 2));

        JPanel left = UiKit.gradient(UiKit.BLUE_DARK, UiKit.BLUE);
        left.setLayout(new GridBagLayout());

        JPanel welcome = new JPanel();
        welcome.setOpaque(false);
        welcome.setLayout(new BoxLayout(welcome, BoxLayout.Y_AXIS));

        welcome.add(brand());
        welcome.add(Box.createVerticalStrut(105));
        welcome.add(UiKit.label("Selamat Datang", 36, true, Color.WHITE));
        welcome.add(UiKit.label("Kembali!", 36, true, UiKit.SKY));
        welcome.add(Box.createVerticalStrut(18));
        welcome.add(UiKit.label(
            "Masuk untuk melaporkan barang hilang, berbagi",
            14, false, Color.WHITE
        ));
        welcome.add(UiKit.label(
            "informasi barang temuan, dan memantau laporanmu.",
            14, false, Color.WHITE
        ));
        welcome.add(Box.createVerticalStrut(30));

        JButton back = UiKit.button(
            "Kembali ke Beranda",
            new Color(255, 255, 255, 35),
            Color.WHITE
        );

        back.setAlignmentX(Component.LEFT_ALIGNMENT);
        back.setBorder(new EmptyBorder(10, 18, 10, 18));
        back.setContentAreaFilled(false);
        back.setOpaque(false);
        back.setHorizontalAlignment(SwingConstants.CENTER);
        back.setForeground(Color.WHITE);
        back.setFocusPainted(false);
        back.setPreferredSize(new Dimension(190, 42));
        back.setMinimumSize(new Dimension(190, 42));
        back.setMaximumSize(new Dimension(190, 42));

        back.addActionListener(e -> {
            new IndexFrame().setVisible(true);
            dispose();
        });

        welcome.add(back);
        left.add(welcome);
        root.add(left);

        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(25, 48, 25, 48));
        form.setPreferredSize(new Dimension(490, 660));

        form.add(UiKit.label("Masuk ke Akun", 29, true, UiKit.TEXT));
        form.add(Box.createVerticalStrut(6));
        form.add(UiKit.label(
            "Pilih jenis akun dan masukkan data login.",
            13, false, UiKit.MUTED
        ));
        form.add(Box.createVerticalStrut(22));

        JLabel roleCaption = UiKit.label(
            "Masuk sebagai", 13, true, UiKit.TEXT
        );
        roleCaption.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(roleCaption);
        form.add(Box.createVerticalStrut(10));

        JPanel roles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        roles.setOpaque(false);
        roles.setAlignmentX(Component.LEFT_ALIGNMENT);
        roles.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        userChoice = roleButton("User", true);
        adminChoice = roleButton("Admin", false);

        userChoice.addActionListener(e -> selectRole("user"));
        adminChoice.addActionListener(e -> selectRole("admin"));

        roles.add(userChoice);
        roles.add(adminChoice);
        form.add(roles);
        form.add(Box.createVerticalStrut(20));

        email = UiKit.field();
        password = UiKit.password();

        form.add(UiKit.fieldBlock("Email", email));
        form.add(Box.createVerticalStrut(15));
        form.add(UiKit.fieldBlock("Password", password));
        form.add(Box.createVerticalStrut(10));

        JLabel req = UiKit.label(
            "Persyaratan password", 12, true, UiKit.TEXT
        );
        req.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(req);

        length = rule("Minimal 8 karakter");
        upper = rule("Mengandung huruf kapital (A-Z)");
        symbol = rule("Mengandung simbol (!@#$%^&*)");

        form.add(length);
        form.add(upper);
        form.add(symbol);

        password.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {

                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    updateRules();
                }

                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    updateRules();
                }

                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    updateRules();
                }
            }
        );

        form.add(Box.createVerticalStrut(20));

        JPanel submitWrap = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 0, 0)
        );
        submitWrap.setOpaque(false);
        submitWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        JButton submit = UiKit.button(
            "Masuk ke FoundIt",
            UiKit.BLUE,
            Color.WHITE
        );

        submit.setPreferredSize(new Dimension(300, 52));
        submit.setMinimumSize(new Dimension(300, 52));
        submit.setMaximumSize(new Dimension(300, 52));

        submit.addActionListener(e -> login());

        submitWrap.add(submit);
        form.add(submitWrap);

        right.add(form);
        root.add(right);

        return root;
    }

    private JButton roleButton(String text, boolean selected) {
        JButton b = new JButton(text) {

            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );

                boolean on = Boolean.TRUE.equals(
                    getClientProperty("selected")
                );

                Color bg = on ? UiKit.BLUE_DARK : UiKit.PALE;

                g2.setColor(getModel().isPressed() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        b.putClientProperty("selected", selected);
        b.setFont(UiKit.MED);
        b.setForeground(selected ? Color.WHITE : UiKit.TEXT);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorder(new EmptyBorder(10, 15, 10, 15));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(
            text.equals("User") ? 78 : 84,
            38
        ));

        return b;
    }

    private void selectRole(String r) {
        role = r;

        styleChoice(userChoice, r.equals("user"));
        styleChoice(adminChoice, r.equals("admin"));
    }

    private void styleChoice(JButton b, boolean selected) {
        b.setForeground(selected ? Color.WHITE : UiKit.TEXT);
        b.putClientProperty("selected", selected);
        b.repaint();
    }

    private JPanel brand() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);

        p.add(UiKit.label("Found", 29, true, Color.WHITE));
        p.add(UiKit.label("It", 29, true, UiKit.SKY));

        return p;
    }

    private JLabel rule(String s) {
        JLabel l = UiKit.label(
            "✕  " + s,
            11,
            false,
            new Color(160, 80, 80)
        );

        l.setBorder(new EmptyBorder(2, 0, 2, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        return l;
    }

    private void updateRules() {
        String p = new String(password.getPassword());

        setRule(length, p.length() >= 8, "Minimal 8 karakter");
        setRule(upper, p.matches(".*[A-Z].*"),
            "Mengandung huruf kapital (A-Z)");
        setRule(symbol, p.matches(".*[!@#$%^&*].*"),
            "Mengandung simbol (!@#$%^&*)");
    }

    private void setRule(JLabel l, boolean ok, String s) {
        l.setText((ok ? "✓  " : "✕  ") + s);
        l.setForeground(
            ok ? new Color(39, 174, 96) : new Color(190, 90, 90)
        );
    }

    private void login() {
        String em = email.getText().trim();
        String pw = new String(password.getPassword());

        if (em.isEmpty() || !em.contains("@")) {
            JOptionPane.showMessageDialog(
                this,
                "Email wajib diisi dan harus mengandung tanda @.",
                "Email belum valid",
                JOptionPane.WARNING_MESSAGE
            );

            email.requestFocus();
            return;
        }

        if (pw.length() < 8
                || !pw.matches(".*[A-Z].*")
                || !pw.matches(".*[!@#$%^&*].*")) {

            JOptionPane.showMessageDialog(
                this,
                "Password belum memenuhi semua persyaratan.",
                "Password belum valid",
                JOptionPane.WARNING_MESSAGE
            );

            password.requestFocus();
            return;
        }

        if (role.equals("admin")) {
            new AdminDashboardFrame().setVisible(true);
        } else {
            new UserDashboardFrame(em, role).setVisible(true);
        }

        dispose();
    }
}