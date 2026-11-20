package com.foundit.apk.view;

import com.foundit.apk.controller.RegisterController;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Halaman Lengkapi Profil / Registrasi Data Mahasiswa (View murni).
 */
public class RegisterFrame extends JFrame {

    private final String email, role;
    private JTextField tfNama, tfNim, tfJurusan, tfEmail, tfNoHp;
    private JComboBox<String> cbFakultas, cbAngkatan;
    private JLabel lblFotoPath;
    private JButton btnChoosePhoto, btnSubmit;
    private final Map<String, Runnable> menuActions = new HashMap<>();

    public RegisterFrame() {
        this("user@campus.ac.id", "USER", true);
    }

    public RegisterFrame(String email, String role) {
        this(email, role, true);
    }

    public RegisterFrame(String email, String role, boolean bindDefaultController) {
        this.email = email;
        this.role = role;

        UiKit.frame(this, "FoundIt | Lengkapi Profil", 1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setContentPane(build());

        if (bindDefaultController) {
            new RegisterController(this, email, role).bind();
        }
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiKit.BG);

        // SIDEBAR NAVIGASI KIRI
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
        side.add(menu("Lapor Barang Hilang", "lost", false, () -> triggerMenu("Lapor Barang Hilang")));
        side.add(menu("Lapor Barang Ditemukan", "found", false, () -> triggerMenu("Lapor Barang Ditemukan")));
        side.add(menu("Riwayat Laporan", "history", false, () -> triggerMenu("Riwayat Laporan")));
        side.add(menu("Status Klaim", "status", false, () -> triggerMenu("Status Klaim")));
        side.add(menu("Lengkapi Profil", "profile", true, () -> {}));

        side.add(Box.createVerticalGlue());

        side.add(menu("Keluar", "return", false, () -> triggerMenu("Keluar")));
        side.add(Box.createVerticalStrut(25));

        root.add(side, BorderLayout.WEST);

        // KONTEN UTAMA
        JPanel mainContent = new JPanel(new GridLayout(1, 2));
        mainContent.setOpaque(false);

        // 1. Panel Info Gradien
        JPanel infoPanel = UiKit.gradient(new Color(44, 62, 80), UiKit.BLUE);
        infoPanel.setBorder(new EmptyBorder(50, 45, 50, 45));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JPanel logoWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoWrap.setOpaque(false);
        logoWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoWrap.add(UiKit.label("Found", 28, true, Color.WHITE));
        logoWrap.add(UiKit.label("It", 28, true, new Color(169, 231, 255)));
        infoPanel.add(logoWrap);
        infoPanel.add(Box.createVerticalStrut(35));

        JLabel titleInfo = UiKit.label("Lengkapi Profilmu.", 34, true, Color.WHITE);
        titleInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(titleInfo);
        infoPanel.add(Box.createVerticalStrut(12));

        JLabel descInfo = new JLabel("<html><div style='width:260px; color:#eaf5fc;'>Lengkapi data diri kamu agar dapat menggunakan seluruh fitur FoundIt.</div></html>");
        descInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(descInfo);
        infoPanel.add(Box.createVerticalStrut(40));

        infoPanel.add(createStepPanel("1", "Data Diri", "Informasi mahasiswa", true));
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(createStepPanel("2", "Profil Selesai", "Mulai menggunakan FoundIt", false));

        mainContent.add(infoPanel);

        // 2. Panel Form Putih
        JPanel rightBox = new JPanel(new BorderLayout());
        rightBox.setBackground(Color.WHITE);
        rightBox.setBorder(new EmptyBorder(35, 45, 35, 45));

        JPanel headerBox = new JPanel();
        headerBox.setOpaque(false);
        headerBox.setLayout(new BoxLayout(headerBox, BoxLayout.Y_AXIS));
        headerBox.add(UiKit.label("Data Mahasiswa", 26, true, UiKit.TEXT));
        headerBox.add(Box.createVerticalStrut(5));
        headerBox.add(UiKit.label("Masukkan data diri kamu dengan benar.", 13, false, UiKit.MUTED));
        headerBox.setBorder(new EmptyBorder(0, 0, 20, 0));

        rightBox.add(headerBox, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        // Foto Profil
        JPanel photoSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        photoSection.setOpaque(false);
        photoSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(238, 238, 238)),
            new EmptyBorder(0, 0, 15, 0)
        ));

        JLabel photoPreview = UiKit.label("👤", 26, true, UiKit.BLUE);
        photoPreview.setHorizontalAlignment(SwingConstants.CENTER);
        photoPreview.setOpaque(true);
        photoPreview.setBackground(new Color(238, 246, 252));
        photoPreview.setPreferredSize(new Dimension(60, 60));
        photoSection.add(photoPreview);

        JPanel photoAction = new JPanel();
        photoAction.setOpaque(false);
        photoAction.setLayout(new BoxLayout(photoAction, BoxLayout.Y_AXIS));
        photoAction.add(UiKit.label("Foto Profil", 13, true, UiKit.TEXT));
        photoAction.add(Box.createVerticalStrut(5));

        btnChoosePhoto = UiKit.button("Pilih File Foto", UiKit.PALE, UiKit.BLUE_DARK);
        photoAction.add(btnChoosePhoto);

        lblFotoPath = UiKit.label("Opsional. Format JPG atau PNG.", 11, false, new Color(153, 153, 153));
        lblFotoPath.setBorder(new EmptyBorder(5, 0, 0, 0));
        photoAction.add(lblFotoPath);

        photoSection.add(photoAction);
        formPanel.add(photoSection);
        formPanel.add(Box.createVerticalStrut(15));

        // Nama Lengkap
        tfNama = UiKit.field();
        formPanel.add(UiKit.fieldBlock("Nama Lengkap", tfNama));
        formPanel.add(Box.createVerticalStrut(12));

        // NIM
        tfNim = UiKit.field();
        formPanel.add(UiKit.fieldBlock("NIM", tfNim));
        formPanel.add(Box.createVerticalStrut(12));

        // Fakultas & Program Studi
        JPanel rowFP = new JPanel(new GridLayout(1, 2, 12, 0));
        rowFP.setOpaque(false);

        cbFakultas = new JComboBox<>(new String[]{
            "Pilih Fakultas",
            "Fakultas Teknik",
            "Fakultas Ekonomi dan Bisnis",
            "Fakultas Hukum",
            "Fakultas Pertanian",
            "Fakultas Ilmu Sosial dan Ilmu Politik",
            "Fakultas Matematika dan Ilmu Pengetahuan Alam",
            "Fakultas Keguruan dan Ilmu Pendidikan"
        });
        cbFakultas.setFont(UiKit.REG);
        cbFakultas.setBackground(Color.WHITE);

        tfJurusan = UiKit.field();

        rowFP.add(UiKit.fieldBlock("Fakultas", cbFakultas));
        rowFP.add(UiKit.fieldBlock("Program Studi", tfJurusan));
        formPanel.add(rowFP);
        formPanel.add(Box.createVerticalStrut(12));

        // Angkatan
        cbAngkatan = new JComboBox<>(new String[]{"Pilih Angkatan", "2022", "2023", "2024", "2025", "2026"});
        cbAngkatan.setFont(UiKit.REG);
        cbAngkatan.setBackground(Color.WHITE);
        formPanel.add(UiKit.fieldBlock("Angkatan", cbAngkatan));
        formPanel.add(Box.createVerticalStrut(12));

        // Email
        tfEmail = UiKit.field();
        tfEmail.setText(email != null ? email : "");
        formPanel.add(UiKit.fieldBlock("Email", tfEmail));
        formPanel.add(Box.createVerticalStrut(12));

        // Nomor WhatsApp
        tfNoHp = UiKit.field();
        formPanel.add(UiKit.fieldBlock("Nomor WhatsApp", tfNoHp));
        formPanel.add(Box.createVerticalStrut(20));

        // Tombol Simpan & Lanjutkan
        btnSubmit = UiKit.button("Simpan & Lanjutkan", UiKit.BLUE, Color.WHITE);
        btnSubmit.setPreferredSize(new Dimension(0, 44));

        JPanel btnWrap = new JPanel(new BorderLayout());
        btnWrap.setOpaque(false);
        btnWrap.add(btnSubmit, BorderLayout.CENTER);
        formPanel.add(btnWrap);

        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.setBorder(null);
        scrollForm.getViewport().setOpaque(false);
        scrollForm.setOpaque(false);
        scrollForm.getVerticalScrollBar().setUnitIncrement(16);

        rightBox.add(scrollForm, BorderLayout.CENTER);
        mainContent.add(rightBox);

        root.add(mainContent, BorderLayout.CENTER);
        return root;
    }

    private JPanel createStepPanel(String num, String title, String subtitle, boolean active) {
        JPanel step = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        step.setOpaque(false);
        step.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel badge = UiKit.label(num, 14, true, UiKit.BLUE);
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBackground(Color.WHITE);
        badge.setPreferredSize(new Dimension(35, 35));

        JPanel textWrap = new JPanel();
        textWrap.setOpaque(false);
        textWrap.setLayout(new BoxLayout(textWrap, BoxLayout.Y_AXIS));

        JLabel tLbl = UiKit.label(title, 14, true, Color.WHITE);
        JLabel sLbl = UiKit.label(subtitle, 12, false, new Color(220, 238, 250));

        if (!active) {
            tLbl.setForeground(new Color(255, 255, 255, 150));
            sLbl.setForeground(new Color(220, 238, 250, 150));
        }

        textWrap.add(tLbl);
        textWrap.add(Box.createVerticalStrut(2));
        textWrap.add(sLbl);

        step.add(badge);
        step.add(textWrap);
        return step;
    }

    private JPanel menu(String text, String icon, boolean active, Runnable action) {
        JPanel p = new JPanel(new BorderLayout());
        p.setMaximumSize(new Dimension(220, 46));
        p.setBorder(new EmptyBorder(12, 18, 12, 10));
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

    public String getNama() { return tfNama.getText(); }
    public void setNama(String text) { tfNama.setText(text); }
    public int getNamaCaretPosition() { return tfNama.getCaretPosition(); }
    public void setNamaCaretPosition(int pos) { try { tfNama.setCaretPosition(pos); } catch (Exception ignored) {} }

    public String getNim() { return tfNim.getText().trim(); }
    public String getFakultas() { return (String) cbFakultas.getSelectedItem(); }
    public String getJurusan() { return tfJurusan.getText().trim(); }
    public String getAngkatan() { return (String) cbAngkatan.getSelectedItem(); }
    public String getEmail() { return tfEmail.getText().trim(); }
    public String getNoHp() { return tfNoHp.getText().trim(); }

    public void setFotoPath(String fileName, Color color) {
        lblFotoPath.setText(fileName);
        lblFotoPath.setForeground(color);
    }

    public void addChoosePhotoListener(ActionListener l) { btnChoosePhoto.addActionListener(l); }
    public void addNamaKeyListener(KeyAdapter k) { tfNama.addKeyListener(k); }
    public void addSubmitListener(ActionListener l) { btnSubmit.addActionListener(l); }
}
