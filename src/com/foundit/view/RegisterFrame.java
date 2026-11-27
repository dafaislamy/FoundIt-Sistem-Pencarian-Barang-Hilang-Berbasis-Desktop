package com.foundit.view;

import com.foundit.controller.AuthController;
import com.foundit.service.ServiceException;
import com.foundit.view.components.RoundedButton;
import com.foundit.view.components.RoundedPanel;
import com.foundit.view.components.RoundedTextField;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * RegisterFrame — Halaman Registrasi User FoundIt Desktop.
 * Desain 2 kolom (info + step indicator di kiri, form data mahasiswa di kanan),
 * daftar fakultas, angkatan, dan validasi password sama persis dengan user/register.php.
 */
public class RegisterFrame extends JFrame {

    private static final String[] DAFTAR_FAKULTAS = {
            "Pilih Fakultas",
            "Fakultas Teknik",
            "Fakultas Ekonomi dan Bisnis",
            "Fakultas Hukum",
            "Fakultas Pertanian",
            "Fakultas Ilmu Sosial dan Ilmu Politik",
            "Fakultas Matematika dan Ilmu Pengetahuan Alam",
            "Fakultas Keguruan dan Ilmu Pendidikan"
    };

    private static final String[] DAFTAR_ANGKATAN = {
            "Pilih Angkatan", "2022", "2023", "2024", "2025", "2026"
    };

    private final AuthController authController = new AuthController();

    private RoundedTextField namaField;
    private RoundedTextField nimField;
    private JComboBox<String> fakultasCombo;
    private RoundedTextField jurusanField;
    private JComboBox<String> angkatanCombo;
    private RoundedTextField emailField;
    private RoundedTextField noHpField;
    private JPasswordField passwordField;
    private JButton togglePasswordBtn;
    private boolean passwordVisible = false;

    private JLabel passwordHintLabel;
    private JLabel errorLabel;
    private RoundedButton submitButton;

    public RegisterFrame() {
        setTitle("Lengkapi Profil - FoundIt");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setMinimumSize(new Dimension(980, 660));
        setLocationRelativeTo(null);
        ThemeConfig.applyMaximized(this);

        setContentPane(buildRoot());
    }

    private JPanel buildRoot() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildLeftInfoPanel(), BorderLayout.WEST);
        root.add(buildRightFormScroll(), BorderLayout.CENTER);
        return root;
    }

    /** Bagian Kiri (.register-info): Gradient navy gelap seragam dengan LoginFrame + 2 steps */
    private JPanel buildLeftInfoPanel() {
        JPanel left = ThemeConfig.createHeroGradientPanel();
        left.setPreferredSize(new Dimension(390, 0));
        left.setLayout(new BorderLayout());
        left.setBorder(new EmptyBorder(48, 44, 40, 44));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Logo FoundIt
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoPanel.setOpaque(false);
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblFound = new JLabel("Found");
        lblFound.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblFound.setForeground(Color.WHITE);
        JLabel lblIt = new JLabel("It");
        lblIt.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblIt.setForeground(ThemeConfig.COLOR_BRAND_LIGHT);
        logoPanel.add(lblFound);
        logoPanel.add(lblIt);
        content.add(logoPanel);
        content.add(Box.createVerticalStrut(42));

        JLabel title = new JLabel("<html>Lengkapi<br>Profilmu.</html>");
        title.setFont(new Font("Segoe UI", Font.BOLD, 40));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(16));

        JLabel desc = new JLabel("<html>Lengkapi data diri kamu agar dapat menggunakan<br>seluruh fitur FoundIt.</html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        desc.setForeground(ThemeConfig.WELCOME_TEXT_SUB);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(desc);
        content.add(Box.createVerticalStrut(36));

        // Steps (.steps)
        content.add(createStepRow("1", "Data Diri", "Informasi mahasiswa", true));
        content.add(Box.createVerticalStrut(22));
     

        left.add(content, BorderLayout.NORTH);

        // Link kembali ke Login
        JLabel backLogin = new JLabel("← Kembali ke Halaman Login");
        backLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backLogin.setForeground(new Color(0xDCEEFA));
        backLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLogin.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { backLogin.setForeground(Color.WHITE); }
            @Override public void mouseExited(MouseEvent e) { backLogin.setForeground(new Color(0xDCEEFA)); }
            @Override public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        left.add(backLogin, BorderLayout.SOUTH);

        return left;
    }

    private JPanel createStepRow(String number, String title, String sub, boolean active) {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel circle = new JLabel(number, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(active ? Color.WHITE : new Color(255, 255, 255, 160));
                g2.fill(new Ellipse2D.Double(0, 0, 35, 35));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        circle.setPreferredSize(new Dimension(35, 35));
        circle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        circle.setForeground(ThemeConfig.COLOR_BRAND);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t.setForeground(active ? Color.WHITE : new Color(255, 255, 255, 180));

        JLabel s = new JLabel(sub);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        s.setForeground(active ? new Color(0xDCEEFA) : new Color(220, 238, 250, 160));

        text.add(t);
        text.add(s);

        row.add(circle, BorderLayout.WEST);
        row.add(text, BorderLayout.CENTER);
        return row;
    }

    /** Bagian Kanan (.register-box): Scrollable Form */
    private JScrollPane buildRightFormScroll() {
        ScrollableFormWrapper outerWrapper = new ScrollableFormWrapper();
        outerWrapper.setBackground(Color.WHITE);
        outerWrapper.setLayout(new BorderLayout());
        outerWrapper.setBorder(new EmptyBorder(32, 64, 32, 64));

        JPanel formContainer = new JPanel();
        formContainer.setBackground(Color.WHITE);
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));

        // Header
        JLabel h2 = new JLabel("Data Mahasiswa");
        h2.setFont(new Font("Segoe UI", Font.BOLD, 26));
        h2.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);
        h2.setAlignmentX(Component.LEFT_ALIGNMENT);
        formContainer.add(h2);
        formContainer.add(Box.createVerticalStrut(4));

        JLabel sub = new JLabel("Masukkan data diri kamu dengan benar.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(ThemeConfig.COLOR_TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        formContainer.add(sub);
        formContainer.add(Box.createVerticalStrut(18));

        // Error banner
        errorLabel = new JLabel();
        errorLabel.setOpaque(true);
        errorLabel.setBackground(ThemeConfig.STATUS_DITOLAK_BG);
        errorLabel.setForeground(ThemeConfig.STATUS_DITOLAK_FG);
        errorLabel.setFont(ThemeConfig.FONT_REGULAR);
        errorLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xFECACA)),
                new EmptyBorder(8, 12, 8, 12)
        ));
        errorLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        errorLabel.setVisible(false);
        formContainer.add(errorLabel);

        // 1. Nama Lengkap (auto-capitalize word initial on focus lost seperti js/register.js)
        namaField = createInput();
        namaField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                namaField.setText(capitalizeWords(namaField.getText()));
            }
        });
        addFormRow(formContainer, "Nama Lengkap", namaField);

        // 2. NIM
        nimField = createInput();
        addFormRow(formContainer, "NIM", nimField);

        // 3. Fakultas & Program Studi (2 kolom)
        JPanel twoCol = new JPanel(new GridLayout(1, 2, 16, 0));
        twoCol.setOpaque(false);
        twoCol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        twoCol.setAlignmentX(Component.LEFT_ALIGNMENT);

        fakultasCombo = createComboBox(DAFTAR_FAKULTAS);
        JPanel fakCol = wrapField("Fakultas", fakultasCombo);

        jurusanField = createInput();
        JPanel jurCol = wrapField("Program Studi", jurusanField);

        twoCol.add(fakCol);
        twoCol.add(jurCol);
        formContainer.add(twoCol);
        formContainer.add(Box.createVerticalStrut(10));

        // 4. Angkatan
        angkatanCombo = createComboBox(DAFTAR_ANGKATAN);
        addFormRow(formContainer, "Angkatan", angkatanCombo);

        // 5. Email
        emailField = createInput();
        addFormRow(formContainer, "Email", emailField);

        // 6. Nomor WhatsApp
        noHpField = createInput();
        addFormRow(formContainer, "Nomor WhatsApp", noHpField);

        // 7. Password + Hint
        formContainer.add(createLabel("Password"));
        formContainer.add(Box.createVerticalStrut(5));
        formContainer.add(buildPasswordGroup());
        formContainer.add(Box.createVerticalStrut(5));

        passwordHintLabel = new JLabel("Minimal 8 karakter, huruf kapital dan simbol.");
        passwordHintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passwordHintLabel.setForeground(ThemeConfig.COLOR_TEXT_MUTED);
        passwordHintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formContainer.add(passwordHintLabel);
        formContainer.add(Box.createVerticalStrut(16));

        // Submit Button (.btn-register)
        submitButton = RoundedButton.createRegisterButton("Simpan & Lanjutkan");
        submitButton.setPreferredSize(new Dimension(240, 42));
        submitButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitButton.addActionListener(e -> handleRegister());
        formContainer.add(submitButton);

        outerWrapper.add(formContainer, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(
                outerWrapper,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private static class ScrollableFormWrapper extends JPanel implements Scrollable {
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) { return 16; }
        @Override public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) { return 64; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }

    private JPanel buildPasswordGroup() {
        RoundedPanel wrapper = new RoundedPanel(7, new Color(0xDDDDDD), 1, false);
        wrapper.setBackground(Color.WHITE);
        wrapper.setLayout(new BorderLayout());
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        wrapper.setPreferredSize(new Dimension(0, 38));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setBorder(new EmptyBorder(8, 12, 8, 6));
        passwordField.setFont(ThemeConfig.FONT_REGULAR);
        passwordField.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);
        passwordField.setOpaque(false);
        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updatePasswordHint(); }
            @Override public void removeUpdate(DocumentEvent e) { updatePasswordHint(); }
            @Override public void changedUpdate(DocumentEvent e) { updatePasswordHint(); }
        });

        togglePasswordBtn = new JButton("👁");
        togglePasswordBtn.setFocusPainted(false);
        togglePasswordBtn.setBorderPainted(false);
        togglePasswordBtn.setContentAreaFilled(false);
        togglePasswordBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        togglePasswordBtn.setForeground(ThemeConfig.COLOR_TEXT_MUTED);
        togglePasswordBtn.setPreferredSize(new Dimension(42, 42));
        togglePasswordBtn.addActionListener(e -> {
            passwordVisible = !passwordVisible;
            passwordField.setEchoChar(passwordVisible ? (char) 0 : '•');
            togglePasswordBtn.setText(passwordVisible ? "🙈" : "👁");
        });

        wrapper.add(passwordField, BorderLayout.CENTER);
        wrapper.add(togglePasswordBtn, BorderLayout.EAST);
        return wrapper;
    }

    private void updatePasswordHint() {
        String pwd = new String(passwordField.getPassword());
        if (pwd.isEmpty()) {
            passwordHintLabel.setText("Minimal 8 karakter, huruf kapital dan simbol.");
            passwordHintLabel.setForeground(ThemeConfig.COLOR_TEXT_MUTED);
        } else if (AuthController.isPasswordValid(pwd)) {
            passwordHintLabel.setText("✓ Password memenuhi syarat (8+ karakter, huruf kapital & simbol).");
            passwordHintLabel.setForeground(ThemeConfig.STATUS_TERBIT_FG);
        } else {
            passwordHintLabel.setText("✕ Minimal 8 karakter, harus mengandung huruf kapital dan simbol.");
            passwordHintLabel.setForeground(ThemeConfig.STATUS_DITOLAK_FG);
        }
    }

    private RoundedTextField createInput() {
        RoundedTextField tf = new RoundedTextField(7);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tf.setPreferredSize(new Dimension(0, 40));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tf;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(ThemeConfig.FONT_REGULAR);
        cb.setBackground(Color.WHITE);
        cb.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cb.setPreferredSize(new Dimension(0, 40));
        cb.setAlignmentX(Component.LEFT_ALIGNMENT);
        return cb;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel wrapField(String labelText, JComponent field) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(createLabel(labelText));
        p.add(Box.createVerticalStrut(5));
        p.add(field);
        return p;
    }

    private void addFormRow(JPanel container, String labelText, JComponent field) {
        container.add(createLabel(labelText));
        container.add(Box.createVerticalStrut(5));
        container.add(field);
        container.add(Box.createVerticalStrut(10));
    }

    private static String capitalizeWords(String input) {
        if (input == null || input.isBlank()) return input;
        String[] words = input.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String w = words[i];
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1));
            }
            if (i < words.length - 1) sb.append(' ');
        }
        return sb.toString();
    }

    private void handleRegister() {
        errorLabel.setVisible(false);

        String nama = capitalizeWords(namaField.getText());
        String nim = nimField.getText().trim();
        String fakultas = fakultasCombo.getSelectedIndex() == 0 ? "" : String.valueOf(fakultasCombo.getSelectedItem());
        String jurusan = jurusanField.getText().trim();
        String angkatan = angkatanCombo.getSelectedIndex() == 0 ? "" : String.valueOf(angkatanCombo.getSelectedItem());
        String email = emailField.getText().trim();
        String noHp = noHpField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            authController.registerUser(nama, nim, fakultas, jurusan, angkatan, email, noHp, password);
            JOptionPane.showMessageDialog(
                    this,
                    "Registrasi berhasil! Silakan login.",
                    "FoundIt - Registrasi Berhasil",
                    JOptionPane.INFORMATION_MESSAGE
            );
            dispose();
            new LoginFrame().setVisible(true);
        } catch (ServiceException ex) {
            errorLabel.setText(ex.getMessage());
            errorLabel.setVisible(true);
        }
    }
}
