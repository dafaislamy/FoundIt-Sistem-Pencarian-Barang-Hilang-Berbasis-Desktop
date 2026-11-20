package com.foundit.view;

import com.foundit.model.Admin;
import com.foundit.model.User;
import com.foundit.service.AdminService;
import com.foundit.service.ServiceException;
import com.foundit.service.UserService;
import com.foundit.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Halaman Login FoundIt terhubung ke UserService & AdminService (Database)[cite: 23, 62].
 */
public class LoginFrame extends JFrame {

    private final UserService userService = new UserService();
    private final AdminService adminService = new AdminService();

    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel lengthRule, upperRule, symbolRule;
    private String selectedRole = "user";
    private JButton userChoiceBtn, adminChoiceBtn;
    private JButton submitBtn, backBtn;

    public LoginFrame() {
        UiKit.frame(this, "FoundIt | Login", 1160, 740);
        setContentPane(build());
        bindEvents();
    }

    private JPanel build() {
        JPanel root = new JPanel(new GridLayout(1, 2));

        // Panel Kiri
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
        welcome.add(UiKit.label("Masuk untuk melaporkan barang hilang, berbagi", 14, false, Color.WHITE));
        welcome.add(UiKit.label("informasi barang temuan, dan memantau laporanmu.", 14, false, Color.WHITE));
        welcome.add(Box.createVerticalStrut(30));

        backBtn = UiKit.button("Kembali ke Beranda", new Color(255, 255, 255, 35), Color.WHITE);
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.setBorder(new EmptyBorder(10, 18, 10, 18));
        backBtn.setContentAreaFilled(false);
        backBtn.setOpaque(false);
        backBtn.setHorizontalAlignment(SwingConstants.CENTER);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setPreferredSize(new Dimension(190, 42));
        backBtn.setMinimumSize(new Dimension(190, 42));
        backBtn.setMaximumSize(new Dimension(190, 42));

        welcome.add(backBtn);
        left.add(welcome);
        root.add(left);

        // Panel Kanan
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(25, 48, 25, 48));
        form.setPreferredSize(new Dimension(490, 660));

        form.add(UiKit.label("Masuk ke Akun", 29, true, UiKit.TEXT));
        form.add(Box.createVerticalStrut(6));
        form.add(UiKit.label("Pilih jenis akun dan masukkan data login.", 13, false, UiKit.MUTED));
        form.add(Box.createVerticalStrut(22));

        JLabel roleCaption = UiKit.label("Masuk sebagai", 13, true, UiKit.TEXT);
        roleCaption.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(roleCaption);
        form.add(Box.createVerticalStrut(10));

        JPanel roles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        roles.setOpaque(false);
        roles.setAlignmentX(Component.LEFT_ALIGNMENT);
        roles.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        userChoiceBtn = roleButton("User", true);
        adminChoiceBtn = roleButton("Admin", false);

        roles.add(userChoiceBtn);
        roles.add(adminChoiceBtn);
        form.add(roles);
        form.add(Box.createVerticalStrut(20));

        emailField = UiKit.field();
        passwordField = UiKit.password();

        form.add(UiKit.fieldBlock("Email", emailField));
        form.add(Box.createVerticalStrut(15));
        form.add(UiKit.fieldBlock("Password", passwordField));
        form.add(Box.createVerticalStrut(10));

        JLabel req = UiKit.label("Persyaratan password", 12, true, UiKit.TEXT);
        req.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(req);

        lengthRule = rule("Minimal 8 karakter");
        upperRule = rule("Mengandung huruf kapital (A-Z)");
        symbolRule = rule("Mengandung simbol (!@#$%^&*)");

        form.add(lengthRule);
        form.add(upperRule);
        form.add(symbolRule);

        form.add(Box.createVerticalStrut(20));

        JPanel submitWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        submitWrap.setOpaque(false);
        submitWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        submitBtn = UiKit.button("Masuk ke FoundIt", UiKit.BLUE, Color.WHITE);
        submitBtn.setPreferredSize(new Dimension(300, 52));
        submitBtn.setMinimumSize(new Dimension(300, 52));
        submitBtn.setMaximumSize(new Dimension(300, 52));

        submitWrap.add(submitBtn);
        form.add(submitWrap);

        right.add(form);
        root.add(right);

        return root;
    }

    private void bindEvents() {
        userChoiceBtn.addActionListener(e -> selectRole("user"));
        adminChoiceBtn.addActionListener(e -> selectRole("admin"));

        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePasswordRules();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePasswordRules();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updatePasswordRules();
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
            new IndexFrame().setVisible(true);
        });

        submitBtn.addActionListener(e -> handleLogin());
        emailField.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
    }

    private void updatePasswordRules() {
        String pw = new String(passwordField.getPassword());
        setRule(lengthRule, "Minimal 8 karakter", pw.length() >= 8);
        setRule(upperRule, "Mengandung huruf kapital (A-Z)", pw.matches(".*[A-Z].*"));
        setRule(symbolRule, "Mengandung simbol (!@#$%^&*)", pw.matches(".*[!@#$%^&*].*"));
    }

    private void setRule(JLabel label, String text, boolean ok) {
        label.setText((ok ? "✓  " : "✕  ") + text);
        label.setForeground(ok ? new Color(39, 174, 96) : new Color(190, 90, 90));
    }

    private void selectRole(String role) {
        this.selectedRole = role;
        styleChoice(userChoiceBtn, "user".equals(role));
        styleChoice(adminChoiceBtn, "admin".equals(role));
    }

    private void styleChoice(JButton b, boolean selected) {
        b.setForeground(selected ? Color.WHITE : UiKit.TEXT);
        b.putClientProperty("selected", selected);
        b.repaint();
    }

    private JButton roleButton(String text, boolean selected) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean on = Boolean.TRUE.equals(getClientProperty("selected"));
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
        b.setPreferredSize(new Dimension(text.equals("User") ? 78 : 84, 38));
        return b;
    }

    private JPanel brand() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.add(UiKit.label("Found", 29, true, Color.WHITE));
        p.add(UiKit.label("It", 29, true, UiKit.SKY));
        return p;
    }

    private JLabel rule(String s) {
        JLabel l = UiKit.label("✕  " + s, 11, false, new Color(160, 80, 80));
        l.setBorder(new EmptyBorder(2, 0, 2, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            if ("admin".equals(selectedRole)) {
                Admin admin = adminService.authenticate(email, password);
                Session.login(admin);
                UiKit.info(this, "Selamat datang, " + admin.getFullName() + "!");
                dispose();
                new MainFrame().setVisible(true);
            } else {
                User user = userService.authenticate(email, password);
                Session.login(user);
                UiKit.info(this, "Selamat datang, " + user.getFullName() + "!");
                dispose();
                new MainFrame().setVisible(true);
            }
        } catch (ServiceException ex) {
            UiKit.warn(this, ex.getMessage());
        }
    }
}