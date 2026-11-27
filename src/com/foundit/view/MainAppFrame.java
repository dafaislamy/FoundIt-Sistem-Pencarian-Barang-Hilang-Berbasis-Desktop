package com.foundit.view;

import com.foundit.controller.AdminController;
import com.foundit.controller.AuthController;
import com.foundit.controller.UserController;
import com.foundit.model.Account;
import com.foundit.model.Admin;
import com.foundit.model.ItemType;
import com.foundit.model.User;
import com.foundit.util.Session;
import com.foundit.view.admin.*;
import com.foundit.view.components.AvatarLabel;
import com.foundit.view.components.RoundedPanel;
import com.foundit.view.components.Sidebar;
import com.foundit.view.user.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MainAppFrame — Kerangka utama aplikasi FoundIt setelah login.
 * Menggunakan satu JFrame tunggal dengan Sidebar di sisi kiri,
 * Topbar dinamis di atas, dan CardLayout di tengah agar perpindahan
 * antar-halaman berjalan mulus tanpa membuka banyak jendela bertumpuk.
 */
public class MainAppFrame extends JFrame {

    private final AuthController authController = new AuthController();
    private final UserController userController = new UserController();
    private final AdminController adminController = new AdminController();
    private final boolean isAdmin;

    private Sidebar sidebar;
    private JLabel topbarTitle;
    private JLabel topbarSubtitle;
    private AvatarLabel topbarAvatar;
    private JLabel topbarNameLabel;
    private CardLayout cardLayout;
    private JPanel contentCards;

    private final Map<String, String[]> routeHeaders = new LinkedHashMap<>();

    // Panel User
    private UserDashboardPanel userDashboardPanel;
    private CariBarangPanel cariBarangPanel;
    private LaporBarangPanel laporHilangPanel;
    private LaporBarangPanel laporDitemukanPanel;
    private KlaimBarangPanel klaimBarangPanel;
    private StatusKlaimPanel statusKlaimPanel;
    private RiwayatLaporanPanel riwayatLaporanPanel;
    private ProfilPanel profilPanel;

    // Panel Admin
    private AdminDashboardPanel adminDashboardPanel;
    private AdminKelolaKategoriPanel adminKelolaKategoriPanel;
    private AdminKelolaLaporanPanel adminKelolaLaporanPanel;
    private AdminKelolaUserPanel adminKelolaUserPanel;
    private AdminUbahStatusPanel adminUbahStatusPanel;
    private AdminVerifikasiKlaimPanel adminVerifikasiKlaimPanel;
    private AdminVerifikasiLaporanPanel adminVerifikasiLaporanPanel;

    public MainAppFrame(Account account) {
        if (account != null) {
            Session.login(account);
        }
        this.isAdmin = (account instanceof Admin)
                || (account instanceof User u && "admin".equalsIgnoreCase(u.getRole()))
                || Session.isAdmin();

        setTitle("FoundIt - " + (isAdmin ? "Dashboard Admin" : "Dashboard User"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 780);
        setMinimumSize(new Dimension(1040, 680));
        setLocationRelativeTo(null);
        ThemeConfig.applyMaximized(this);

        initRouteHeaders();
        setContentPane(buildShell());

        String initialRoute = isAdmin ? "admin-dashboard" : "dashboard";
        navigateTo(initialRoute);
    }

    private void initRouteHeaders() {
        if (isAdmin) {
            routeHeaders.put("admin-dashboard", new String[]{
                    "Dashboard Admin",
                    "Selamat datang di panel kendali sistem pencarian barang kampus FoundIt."
            });
            routeHeaders.put("admin-kelola-kategori", new String[]{
                    "Kelola Kategori",
                    "Tambah, ubah, dan hapus kategori barang hilang maupun ditemukan."
            });
            routeHeaders.put("admin-kelola-laporan", new String[]{
                    "Kelola Laporan",
                    "Daftar seluruh laporan barang hilang dan ditemukan di kampus."
            });
            routeHeaders.put("admin-kelola-user", new String[]{
                    "Kelola User",
                    "Lihat dan kelola akun mahasiswa terdaftar pada sistem FoundIt."
            });
            routeHeaders.put("admin-ubah-status", new String[]{
                    "Ubah Status",
                    "Perbarui status publikasi atau penyelesaian laporan barang."
            });
            routeHeaders.put("admin-verifikasi-klaim", new String[]{
                    "Verifikasi Klaim",
                    "Cocokkan bukti kepemilikan barang dari pemohon klaim."
            });
            routeHeaders.put("admin-verifikasi-laporan", new String[]{
                    "Verifikasi Laporan",
                    "Tinjau laporan barang yang berstatus Pending sebelum diterbitkan."
            });
        } else {
            String nama = Session.getDisplayName();
            routeHeaders.put("dashboard", new String[]{
                    "Selamat datang, " + nama + "!",
                    "Pantau status laporan barang hilang, temukan barang tertinggal, dan kelola klaim Anda."
            });
            routeHeaders.put("cari-barang", new String[]{
                    "Cari Barang",
                    "Temukan barang hilang atau ditemukan yang telah diterbitkan di kampus."
            });
            routeHeaders.put("lapor-hilang", new String[]{
                    "Lapor Barang Hilang",
                    "Isi formulir laporan barang milik Anda yang hilang di area kampus."
            });
            routeHeaders.put("lapor-ditemukan", new String[]{
                    "Lapor Barang Ditemukan",
                    "Laporkan barang yang Anda temukan agar segera kembali ke pemiliknya."
            });
            routeHeaders.put("klaim", new String[]{
                    "Klaim Barang",
                    "Ajukan klaim kepemilikan dengan melampirkan bukti yang valid."
            });
            routeHeaders.put("status-klaim", new String[]{
                    "Status Klaim",
                    "Pantau perkembangan verifikasi klaim barang yang pernah Anda ajukan."
            });
            routeHeaders.put("riwayat-laporan", new String[]{
                    "Riwayat Laporan",
                    "Lihat daftar seluruh laporan barang yang pernah Anda buat."
            });
            routeHeaders.put("profil", new String[]{
                    "Profil",
                    "Lihat dan perbarui informasi akun mahasiswa Anda."
            });
        }
    }

    private JPanel buildShell() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ThemeConfig.BG_PAGE);

        // 1. SIDEBAR (WEST) — urutan menu sama persis seperti versi web
        sidebar = new Sidebar();
        if (isAdmin) {
            sidebar.addMenuItem("Dashboard", "admin-dashboard", true);
            sidebar.addMenuItem("Kelola Kategori", "admin-kelola-kategori", false);
            sidebar.addMenuItem("Kelola Laporan", "admin-kelola-laporan", false);
            sidebar.addMenuItem("Kelola User", "admin-kelola-user", false);
            sidebar.addMenuItem("Ubah Status", "admin-ubah-status", false);
            sidebar.addMenuItem("Verifikasi Klaim", "admin-verifikasi-klaim", false);
            sidebar.addMenuItem("Verifikasi Laporan", "admin-verifikasi-laporan", false);
        } else {
            sidebar.addMenuItem("Dashboard", "dashboard", true);
            sidebar.addMenuItem("Cari Barang", "cari-barang", false);
            sidebar.addMenuItem("Lapor Barang Hilang", "lapor-hilang", false);
            sidebar.addMenuItem("Lapor Barang Ditemukan", "lapor-ditemukan", false);
            sidebar.addMenuItem("Klaim Barang", "klaim", false);
            sidebar.addMenuItem("Status Klaim", "status-klaim", false);
            sidebar.addMenuItem("Riwayat Laporan", "riwayat-laporan", false);
            sidebar.addMenuItem("Profil", "profil", false);
        }

        sidebar.setOnNavigate(this::navigateTo);
        sidebar.addLogoutButton(() -> {
            authController.logout();
            dispose();
            new LoginFrame().setVisible(true);
        });
        root.add(sidebar, BorderLayout.WEST);

        // 2. MAIN CONTENT (CENTER) — Topbar + CardLayout
        JPanel mainContent = new JPanel(new BorderLayout(0, 22));
        mainContent.setBackground(ThemeConfig.BG_PAGE);
        mainContent.setBorder(new EmptyBorder(24, 34, 24, 34));

        mainContent.add(buildTopbar(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setOpaque(false);

        buildAllCards();
        mainContent.add(contentCards, BorderLayout.CENTER);

        root.add(mainContent, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildTopbar() {
        JPanel topbar = new JPanel(new BorderLayout(20, 0));
        topbar.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        topbarTitle = new JLabel("Dashboard");
        topbarTitle.setFont(ThemeConfig.FONT_HEADING);
        topbarTitle.setForeground(new Color(0x1E293B));

        topbarSubtitle = new JLabel("");
        topbarSubtitle.setFont(ThemeConfig.FONT_MEDIUM);
        topbarSubtitle.setForeground(ThemeConfig.COLOR_TEXT_MUTED);

        titleBox.add(topbarTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(topbarSubtitle);
        topbar.add(titleBox, BorderLayout.WEST);

        // User / Admin Profile Badge (.admin-user-badge / .user-profile)
        RoundedPanel profileBadge = new RoundedPanel(30, ThemeConfig.BORDER_CARD, 1, false);
        profileBadge.setBackground(Color.WHITE);
        profileBadge.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        profileBadge.setBorder(new EmptyBorder(2, 8, 2, 12));

        topbarAvatar = new AvatarLabel(Session.getInitial(), 38);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        topbarNameLabel = new JLabel(Session.getDisplayName());
        topbarNameLabel.setFont(ThemeConfig.FONT_SEMIBOLD);
        topbarNameLabel.setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);

        JLabel roleLbl = new JLabel(isAdmin ? "Super Admin" : "User");
        roleLbl.setFont(ThemeConfig.FONT_SMALL);
        roleLbl.setForeground(ThemeConfig.COLOR_TEXT_MUTED);

        info.add(topbarNameLabel);
        info.add(roleLbl);

        profileBadge.add(topbarAvatar);
        profileBadge.add(info);

        JPanel rightWrap = new JPanel(new GridBagLayout());
        rightWrap.setOpaque(false);
        rightWrap.add(profileBadge);
        topbar.add(rightWrap, BorderLayout.EAST);

        return topbar;
    }

    private void buildAllCards() {
        if (!isAdmin) {
            userDashboardPanel = new UserDashboardPanel(userController, this::navigateTo);
            cariBarangPanel = new CariBarangPanel(userController, itemId -> {
                klaimBarangPanel.preselectItem(itemId);
                navigateTo("klaim");
            });
            laporHilangPanel = new LaporBarangPanel(userController, ItemType.LOST, this::navigateTo);
            laporDitemukanPanel = new LaporBarangPanel(userController, ItemType.FOUND, this::navigateTo);
            klaimBarangPanel = new KlaimBarangPanel(userController, this::navigateTo);
            statusKlaimPanel = new StatusKlaimPanel(userController, this::navigateTo);
            riwayatLaporanPanel = new RiwayatLaporanPanel(userController, this::navigateTo);
            profilPanel = new ProfilPanel(userController, this::refreshTopbarProfile);

            contentCards.add(userDashboardPanel, "dashboard");
            contentCards.add(cariBarangPanel, "cari-barang");
            contentCards.add(laporHilangPanel, "lapor-hilang");
            contentCards.add(laporDitemukanPanel, "lapor-ditemukan");
            contentCards.add(klaimBarangPanel, "klaim");
            contentCards.add(statusKlaimPanel, "status-klaim");
            contentCards.add(riwayatLaporanPanel, "riwayat-laporan");
            contentCards.add(profilPanel, "profil");
        } else {
            adminDashboardPanel = new AdminDashboardPanel(adminController, this::navigateTo);
            adminKelolaKategoriPanel = new AdminKelolaKategoriPanel(adminController);
            adminKelolaLaporanPanel = new AdminKelolaLaporanPanel(adminController);
            adminKelolaUserPanel = new AdminKelolaUserPanel(adminController);
            adminUbahStatusPanel = new AdminUbahStatusPanel(adminController);
            adminVerifikasiKlaimPanel = new AdminVerifikasiKlaimPanel(adminController);
            adminVerifikasiLaporanPanel = new AdminVerifikasiLaporanPanel(adminController);

            contentCards.add(adminDashboardPanel, "admin-dashboard");
            contentCards.add(adminKelolaKategoriPanel, "admin-kelola-kategori");
            contentCards.add(adminKelolaLaporanPanel, "admin-kelola-laporan");
            contentCards.add(adminKelolaUserPanel, "admin-kelola-user");
            contentCards.add(adminUbahStatusPanel, "admin-ubah-status");
            contentCards.add(adminVerifikasiKlaimPanel, "admin-verifikasi-klaim");
            contentCards.add(adminVerifikasiLaporanPanel, "admin-verifikasi-laporan");
        }
    }

    private void refreshTopbarProfile() {
        topbarAvatar.setInisial(Session.getInitial());
        topbarNameLabel.setText(Session.getDisplayName());
        routeHeaders.put("dashboard", new String[]{
                "Selamat datang, " + Session.getDisplayName() + "!",
                "Pantau status laporan barang hilang, temukan barang tertinggal, dan kelola klaim Anda."
        });
    }

    /** Berpindah antar-halaman di dalam CardLayout, memperbarui Topbar, dan me-refresh data dari MySQL */
    public void navigateTo(String routeKey) {
        String[] header = routeHeaders.get(routeKey);
        if (header != null) {
            topbarTitle.setText(header[0]);
            topbarSubtitle.setText(header[1]);
            setTitle("FoundIt - " + header[0]);
        }

        if (!isAdmin) {
            switch (routeKey) {
                case "dashboard" -> userDashboardPanel.refreshData();
                case "cari-barang" -> cariBarangPanel.refreshData();
                case "lapor-hilang" -> laporHilangPanel.refreshData();
                case "lapor-ditemukan" -> laporDitemukanPanel.refreshData();
                case "klaim" -> klaimBarangPanel.refreshData();
                case "status-klaim" -> statusKlaimPanel.refreshData();
                case "riwayat-laporan" -> riwayatLaporanPanel.refreshData();
                case "profil" -> profilPanel.refreshData();
            }
        } else {
            switch (routeKey) {
                case "admin-dashboard" -> adminDashboardPanel.refreshData();
                case "admin-kelola-kategori" -> adminKelolaKategoriPanel.refreshData();
                case "admin-kelola-laporan" -> adminKelolaLaporanPanel.refreshData();
                case "admin-kelola-user" -> adminKelolaUserPanel.refreshData();
                case "admin-ubah-status" -> adminUbahStatusPanel.refreshData();
                case "admin-verifikasi-klaim" -> adminVerifikasiKlaimPanel.refreshData();
                case "admin-verifikasi-laporan" -> adminVerifikasiLaporanPanel.refreshData();
            }
        }

        cardLayout.show(contentCards, routeKey);
    }
}
