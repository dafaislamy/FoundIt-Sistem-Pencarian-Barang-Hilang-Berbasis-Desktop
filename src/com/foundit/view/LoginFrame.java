package com.foundit.view;

import com.foundit.controller.AuthController;
import com.foundit.model.Account;
import com.foundit.service.ServiceException;
import com.foundit.view.components.RoundedButton;
import com.foundit.view.components.RoundedPanel;
import com.foundit.view.components.RoundedTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * LoginFrame — Halaman Login FoundIt Desktop.
 * Desain, pilihan role (User/Admin), toggle password, dan alur login
 * diselaraskan 1:1 dengan versi web (login.php + js/login.js + css/main.css).
 */
public class LoginFrame extends JFrame {

    private final AuthController authController = new AuthController();

    private String selectedRole = "user";
    private RoleCardPanel userRoleCard;
    private RoleCardPanel adminRoleCard;

    private RoundedTextField emailField;
    private JPasswordField passwordField;
    private JButton togglePasswordBtn;
    private boolean passwordVisible = false;

    private RoundedButton loginButton;
    private JPanel registerNotePanel;
    private JLabel errorLabel;

    public LoginFrame() {
        setTitle("Login - FoundIt");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1160, 740);
        setMinimumSize(new Dimension(960, 640));
        setLocationRelativeTo(null);

        setContentPane(buildRoot());
        updateRoleUI();
    }

    private JPanel buildRoot() {
        JPanel root = new JPanel(new GridLayout(1, 2));
        root.add(buildLeftInfoPanel());
        root.add(buildRightLoginBox());
        return root;
    }

    /** Bagian Kiri (.login-info): Gradient #1e293b -> #0f172a -> #1e3a8a */
    private JPanel buildLeftInfoPanel() {
        JPanel left = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                LinearGradientPaint gp = new LinearGradientPaint(
                        0, 0, w, h,
                        new float[]{0f, 0.45f, 1f},
                        new Color[]{
                                ThemeConfig.HERO_GRAD_START,
                                ThemeConfig.HERO_GRAD_MID,
                                ThemeConfig.HERO_GRAD_END
                        }
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);
                g2.dispose();
            }
        };
        left.setLayout(new GridBagLayout());

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(40, 60, 40, 60));

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
        content.add(Box.createVerticalStrut(48));

        JLabel title1 = new JLabel("<html>Temukan Kembali<br>Barangmu.</html>");
        title1.setFont(new Font("Segoe UI", Font.BOLD, 42));
        title1.setForeground(Color.WHITE);
        title1.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title1);
        content.add(Box.createVerticalStrut(18));

        JLabel subtitle = new JLabel("<html>Platform barang hilang dan ditemukan<br>untuk seluruh civitas kampus.</html>");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(ThemeConfig.HERO_TEXT_SUB);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createVerticalStrut(32));

        JLabel backHome = new JLabel("← Kembali ke Beranda");
        backHome.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backHome.setForeground(new Color(0xBAE6FD));
        backHome.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backHome.setAlignmentX(Component.LEFT_ALIGNMENT);
        backHome.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { backHome.setForeground(Color.WHITE); }
            @Override public void mouseExited(MouseEvent e) { backHome.setForeground(new Color(0xBAE6FD)); }
            @Override public void mouseClicked(MouseEvent e) {
                dispose();
                new IndexFrame().setVisible(true);
            }
        });
        content.add(backHome);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        left.add(content, gbc);
        return left;
    }

    /** Bagian Kanan (.login-box): Form Login */
    private JPanel buildRightLoginBox() {
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(40, 60, 40, 60));
        form.setPreferredSize(new Dimension(520, 600));

        // Header
        JLabel headerTitle = new JLabel("Selamat Datang");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        headerTitle.setForeground(new Color(0x0F172A));
        headerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(headerTitle);
        form.add(Box.createVerticalStrut(6));

        JLabel headerSub = new JLabel("Silakan pilih akun untuk masuk ke FoundIt.");
        headerSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        headerSub.setForeground(ThemeConfig.COLOR_TEXT_MUTED);
        headerSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(headerSub);
        form.add(Box.createVerticalStrut(22));

        // Alert Error (hidden by default)
        errorLabel = new JLabel();
        errorLabel.setOpaque(true);
        errorLabel.setBackground(ThemeConfig.STATUS_DITOLAK_BG);
        errorLabel.setForeground(ThemeConfig.STATUS_DITOLAK_FG);
        errorLabel.setFont(ThemeConfig.FONT_REGULAR);
        errorLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xFECACA)),
                new EmptyBorder(10, 14, 10, 14)
        ));
        errorLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        errorLabel.setVisible(false);
        form.add(errorLabel);

        // Role Selection Cards (.role-selection)
        JPanel rolePanel = new JPanel(new GridLayout(1, 2, 14, 0));
        rolePanel.setOpaque(false);
        rolePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        rolePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        userRoleCard = new RoleCardPanel("👤", "User", "Cari dan laporkan barang");
        adminRoleCard = new RoleCardPanel("🛠️", "Admin", "Kelola FoundIt");

        userRoleCard.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                selectedRole = "user";
                updateRoleUI();
            }
        });
        adminRoleCard.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                selectedRole = "admin";
                updateRoleUI();
            }
        });

        rolePanel.add(userRoleCard);
        rolePanel.add(adminRoleCard);
        form.add(rolePanel);
        form.add(Box.createVerticalStrut(24));

        // Email Field
        form.add(createFieldLabel("Email"));
        form.add(Box.createVerticalStrut(7));
        emailField = new RoundedTextField(ThemeConfig.INPUT_RADIUS);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        emailField.setPreferredSize(new Dimension(0, 44));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField.addActionListener(e -> handleLogin());
        form.add(emailField);
        form.add(Box.createVerticalStrut(18));

        // Password Field + Toggle Eye Button
        form.add(createFieldLabel("Password"));
        form.add(Box.createVerticalStrut(7));
        form.add(buildPasswordGroup());
        form.add(Box.createVerticalStrut(24));

        // Submit Button
        loginButton = RoundedButton.createLoginButton("Login sebagai User");
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.addActionListener(e -> handleLogin());
        form.add(loginButton);
        form.add(Box.createVerticalStrut(20));

        // Register Note (hanya tampil saat role == user)
        registerNotePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerNotePanel.setOpaque(false);
        registerNotePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        registerNotePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel belumLabel = new JLabel("Belum memiliki akun?");
        belumLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        belumLabel.setForeground(ThemeConfig.COLOR_TEXT_MUTED);

        JLabel daftarLink = new JLabel("Daftar sebagai User");
        daftarLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
        daftarLink.setForeground(ThemeConfig.COLOR_BRAND_DARK);
        daftarLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        daftarLink.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                daftarLink.setForeground(ThemeConfig.COLOR_BRAND_DARKER);
                daftarLink.setText("<html><u>Daftar sebagai User</u></html>");
            }
            @Override public void mouseExited(MouseEvent e) {
                daftarLink.setForeground(ThemeConfig.COLOR_BRAND_DARK);
                daftarLink.setText("Daftar sebagai User");
            }
            @Override public void mouseClicked(MouseEvent e) {
                dispose();
                new RegisterFrame().setVisible(true);
            }
        });

        registerNotePanel.add(belumLabel);
        registerNotePanel.add(daftarLink);
        form.add(registerNotePanel);

        right.add(form);
        return right;
    }

    private JPanel buildPasswordGroup() {
        RoundedPanel wrapper = new RoundedPanel(ThemeConfig.INPUT_RADIUS, ThemeConfig.INPUT_BORDER, 1, false);
        wrapper.setBackground(Color.WHITE);
        wrapper.setLayout(new BorderLayout());
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        wrapper.setPreferredSize(new Dimension(0, 44));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setBorder(new EmptyBorder(10, 13, 10, 6));
        passwordField.setFont(ThemeConfig.FONT_MEDIUM);
        passwordField.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);
        passwordField.setOpaque(false);
        passwordField.addActionListener(e -> handleLogin());

        togglePasswordBtn = new JButton("👁");
        togglePasswordBtn.setFocusPainted(false);
        togglePasswordBtn.setBorderPainted(false);
        togglePasswordBtn.setContentAreaFilled(false);
        togglePasswordBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        togglePasswordBtn.setForeground(ThemeConfig.COLOR_TEXT_MUTED);
        togglePasswordBtn.setPreferredSize(new Dimension(44, 44));
        togglePasswordBtn.setToolTipText("Tampilkan password");
        togglePasswordBtn.addActionListener(e -> togglePasswordVisibility());

        wrapper.add(passwordField, BorderLayout.CENTER);
        wrapper.add(togglePasswordBtn, BorderLayout.EAST);
        return wrapper;
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        passwordField.setEchoChar(passwordVisible ? (char) 0 : '•');
        togglePasswordBtn.setText(passwordVisible ? "🙈" : "👁");
        togglePasswordBtn.setToolTipText(passwordVisible ? "Sembunyikan password" : "Tampilkan password");
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(ThemeConfig.INPUT_LABEL_FG);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void updateRoleUI() {
        boolean isUser = "user".equals(selectedRole);
        userRoleCard.setActive(isUser);
        adminRoleCard.setActive(!isUser);
        loginButton.setText(isUser ? "Login sebagai User" : "Login sebagai Admin");
        registerNotePanel.setVisible(isUser);
        errorLabel.setVisible(false);
    }

    private void handleLogin() {
        errorLabel.setVisible(false);
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try {
            Account account = authController.login(email, password, selectedRole);
            dispose();
            new MainAppFrame(account).setVisible(true);
        } catch (ServiceException ex) {
            errorLabel.setText(ex.getMessage());
            errorLabel.setVisible(true);
        }
    }

    // ===== Inner class: RoleCardPanel (.role-card) =====
    private static class RoleCardPanel extends JPanel {
        private boolean active = false;
        private boolean hovered = false;

        RoleCardPanel(String icon, String title, String desc) {
            setOpaque(false);
            setLayout(new BorderLayout(12, 0));
            setBorder(new EmptyBorder(12, 14, 12, 14));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel iconLbl = new JLabel(icon);
            iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
            add(iconLbl, BorderLayout.WEST);

            JPanel textPanel = new JPanel();
            textPanel.setOpaque(false);
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

            JLabel t = new JLabel(title);
            t.setFont(new Font("Segoe UI", Font.BOLD, 15));
            t.setForeground(new Color(0x0F172A));

            JLabel d = new JLabel(desc);
            d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            d.setForeground(ThemeConfig.COLOR_TEXT_MUTED);

            textPanel.add(t);
            textPanel.add(Box.createVerticalStrut(2));
            textPanel.add(d);
            add(textPanel, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
            });
        }

        void setActive(boolean active) {
            this.active = active;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bg = active ? new Color(0xF0F9FF) : Color.WHITE;
            Color border = active ? ThemeConfig.COLOR_BRAND_DARK
                    : (hovered ? ThemeConfig.COLOR_BRAND_DARK : new Color(0xE2E8F0));
            float stroke = active ? 2.0f : 1.5f;

            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));

            g2.setColor(border);
            g2.setStroke(new BasicStroke(stroke));
            g2.draw(new RoundRectangle2D.Double(stroke / 2, stroke / 2,
                    getWidth() - stroke, getHeight() - stroke, 12, 12));

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
