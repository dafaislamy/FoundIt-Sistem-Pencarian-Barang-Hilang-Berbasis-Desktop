package com.foundit.view;

import java.awt.*;

/**
 * ThemeConfig — pusat konfigurasi tema visual aplikasi FoundIt Desktop.
 *
 * Seluruh warna, font, ukuran, border-radius, dan spasi diekstrak secara
 * 1:1 dari file CSS web versi FoundIt (css/main.css).
 *
 * Referensi CSS web:
 *   body              → BG_PAGE, COLOR_TEXT_PRIMARY
 *   .sidebar          → BG_SIDEBAR, SIDEBAR_WIDTH
 *   .navbar           → BG_NAVBAR, NAVBAR_HEIGHT
 *   .hero (login-info)→ GRADIENT start/end: #1e293b → #0f172a → #1e3a8a
 *   .logo span        → COLOR_BRAND_ACCENT (#3498db / #38bdf8)
 *   .btn-login        → BUTTON_PRIMARY (#0284c7)
 *   .stat-card        → BG_CARD, BORDER_CARD
 *   .welcome-card     → GRADIENT_WELCOME start/end
 *   Badge tipe hilang → #ffebee / #c62828
 *   Badge ditemukan   → #e8f5e9 / #2e7d32
 *   Badge status      → see STATUS_* constants
 */
public final class ThemeConfig {

    // ===== FONT =====
    /** Font utama (Segoe UI mirip web — fallback ke SansSerif / Dialog) */
    public static final Font FONT_REGULAR   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_MEDIUM    = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SEMIBOLD  = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FONT_BOLD      = new Font("Segoe UI", Font.BOLD,  15);
    public static final Font FONT_TITLE     = new Font("Segoe UI", Font.BOLD,  20);
    public static final Font FONT_HEADING   = new Font("Segoe UI", Font.BOLD,  26);
    public static final Font FONT_LOGO      = new Font("Segoe UI", Font.BOLD,  28);
    public static final Font FONT_SMALL     = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_STAT_NUM  = new Font("Segoe UI", Font.BOLD,  24);
    public static final Font FONT_BADGE     = new Font("Segoe UI", Font.BOLD,  11);

    // ===== WARNA LATAR HALAMAN & GLOBAL =====
    /** body background-color: #f7f9fc */
    public static final Color BG_PAGE              = new Color(0xF7F9FC);
    /** body color: #2c3e50 */
    public static final Color COLOR_TEXT_PRIMARY   = new Color(0x2C3E50);
    /** .topbar p color / muted text: #777 / #64748b */
    public static final Color COLOR_TEXT_MUTED     = new Color(0x64748B);
    /** .section-header p: #888 */
    public static final Color COLOR_TEXT_LIGHT     = new Color(0x888888);

    // ===== NAVBAR =====
    /** .navbar background: #ffffff */
    public static final Color BG_NAVBAR     = Color.WHITE;
    /** navbar height: 70px */
    public static final int   NAVBAR_HEIGHT = 70;
    /** .navbar box-shadow → gunakan border bottom tipis di Swing */
    public static final Color BORDER_NAVBAR = new Color(0xE5E7EB);

    // ===== SIDEBAR =====
    /** .sidebar background: #ffffff */
    public static final Color BG_SIDEBAR       = Color.WHITE;
    /** .sidebar border-right: 1px solid #e5e7eb */
    public static final Color BORDER_SIDEBAR   = new Color(0xE5E7EB);
    /** .sidebar width: 240px */
    public static final int   SIDEBAR_WIDTH    = 240;
    /** .sidebar padding: 30px 20px */
    public static final int   SIDEBAR_PADDING  = 20;

    // ===== SIDEBAR MENU ITEMS =====
    /** .sidebar nav a color: #667085 */
    public static final Color SIDEBAR_ITEM_FG        = new Color(0x667085);
    /** .sidebar nav a:hover bg: #f0f7fc */
    public static final Color SIDEBAR_ITEM_HOVER_BG  = new Color(0xF0F7FC);
    /** .sidebar nav a.active bg: #eaf5fc */
    public static final Color SIDEBAR_ITEM_ACTIVE_BG = new Color(0xEAF5FC);
    /** .sidebar nav a.active color / hover color: #3498db */
    public static final Color SIDEBAR_ITEM_ACTIVE_FG = new Color(0x3498DB);
    /** .sidebar nav a border-radius: 8px */
    public static final int   SIDEBAR_ITEM_RADIUS    = 8;
    /** sidebar nav a padding: 12px 14px */
    public static final Insets SIDEBAR_ITEM_PADDING  = new Insets(12, 14, 12, 14);

    // ===== LOGOUT BUTTON =====
    /** .logout color: #e74c3c */
    public static final Color LOGOUT_FG       = new Color(0xE74C3C);
    /** .logout:hover bg: #fdf0ef */
    public static final Color LOGOUT_HOVER_BG = new Color(0xFDF0EF);

    // ===== BRAND / ACCENT =====
    /** .logo span / .nav-link:hover / .sidebar nav a.active → #3498db */
    public static final Color COLOR_BRAND        = new Color(0x3498DB);
    /** .hero h1 span / .footer h2 span / .login-info .logo span → #38bdf8 */
    public static final Color COLOR_BRAND_LIGHT  = new Color(0x38BDF8);
    /** btn-login background: #0284c7 */
    public static final Color COLOR_BRAND_DARK   = new Color(0x0284C7);
    /** btn-login:hover: #0369a1 */
    public static final Color COLOR_BRAND_DARKER = new Color(0x0369A1);
    /** .btn-card-klaim:hover / .btn-login:hover: #2980b9 */
    public static final Color COLOR_BRAND_MED    = new Color(0x2980B9);

    // ===== HERO / LOGIN PANEL GRADIENT =====
    /** .hero / .login-info gradient mulai: #1e293b */
    public static final Color HERO_GRAD_START   = new Color(0x1E293B);
    /** .hero / .login-info gradient tengah: #0f172a */
    public static final Color HERO_GRAD_MID     = new Color(0x0F172A);
    /** .hero / .login-info gradient akhir: #1e3a8a */
    public static final Color HERO_GRAD_END     = new Color(0x1E3A8A);
    /** hero subtitle / login-info p: #cbd5e1 */
    public static final Color HERO_TEXT_SUB     = new Color(0xCBD5E1);

    // ===== WELCOME CARD (dashboard) =====
    /** .welcome-card gradient mulai: #1e293b */
    public static final Color WELCOME_GRAD_START = new Color(0x1E293B);
    /** .welcome-card gradient akhir: #1e3a8a */
    public static final Color WELCOME_GRAD_END   = new Color(0x1E3A8A);
    /** .welcome-card p color: #eaf5fc */
    public static final Color WELCOME_TEXT_SUB   = new Color(0xEAF5FC);

    // ===== CARD BARANG & STAT =====
    /** .stat-card background: white; .grid-barang .card bg: white */
    public static final Color BG_CARD        = Color.WHITE;
    /** .stat-card border / .grid-barang .card border: #e5e7eb */
    public static final Color BORDER_CARD    = new Color(0xE5E7EB);
    /** .grid-barang .card border-radius: 12px */
    public static final int   CARD_RADIUS    = 12;
    /** .fitur-box border-radius: 18px */
    public static final int   CARD_RADIUS_LG = 18;
    /** .stat-card box-shadow approx opacity */
    public static final Color SHADOW_COLOR   = new Color(0, 0, 0, 25);

    // ===== STAT ICON CIRCLES =====
    /** .stat-icon / .action-icon bg: #eef6fc */
    public static final Color BG_ICON_DEFAULT  = new Color(0xEEF6FC);
    /** .stat-circle-blue: bg #dbeafe / fg #0284c7 */
    public static final Color BG_STAT_BLUE     = new Color(0xDBEAFE);
    public static final Color FG_STAT_BLUE     = new Color(0x0284C7);
    /** .stat-circle-purple / .ikon-royal: bg #e0e7ff / fg #4338ca */
    public static final Color BG_STAT_PURPLE   = new Color(0xE0E7FF);
    public static final Color FG_STAT_PURPLE   = new Color(0x4338CA);
    /** .stat-circle-amber: bg #fef3c7 / fg #d97706 */
    public static final Color BG_STAT_AMBER    = new Color(0xFEF3C7);
    public static final Color FG_STAT_AMBER    = new Color(0xD97706);
    /** .stat-circle-emerald: bg #dcfce7 / fg #15803d */
    public static final Color BG_STAT_EMERALD  = new Color(0xDCFCE7);
    public static final Color FG_STAT_EMERALD  = new Color(0x15803D);

    // ===== BADGE TIPE BARANG =====
    /** .hilang: bg #ffebee / fg #c62828 */
    public static final Color BADGE_LOST_BG = new Color(0xFFEBEE);
    public static final Color BADGE_LOST_FG = new Color(0xC62828);
    /** .ditemukan: bg #e8f5e9 / fg #2e7d32 */
    public static final Color BADGE_FOUND_BG = new Color(0xE8F5E9);
    public static final Color BADGE_FOUND_FG = new Color(0x2E7D32);

    // ===== BADGE STATUS ITEM =====
    /** Pending: amber */
    public static final Color STATUS_PENDING_BG  = new Color(0xFFF4E5);
    public static final Color STATUS_PENDING_FG  = new Color(0xD68910);
    /** Terbit: hijau */
    public static final Color STATUS_TERBIT_BG   = new Color(0xEAF8EF);
    public static final Color STATUS_TERBIT_FG   = new Color(0x27AE60);
    /** Selesai: biru */
    public static final Color STATUS_SELESAI_BG  = new Color(0xE0F2FE);
    public static final Color STATUS_SELESAI_FG  = new Color(0x0284C7);
    /** Ditolak: merah */
    public static final Color STATUS_DITOLAK_BG  = new Color(0xFEE2E2);
    public static final Color STATUS_DITOLAK_FG  = new Color(0xDC2626);
    /** Arsip: abu */
    public static final Color STATUS_ARSIP_BG    = new Color(0xF1F5F9);
    public static final Color STATUS_ARSIP_FG    = new Color(0x64748B);

    // ===== BADGE STATUS KLAIM =====
    /** Menunggu */
    public static final Color STATUS_MENUNGGU_BG   = new Color(0xFFF4E5);
    public static final Color STATUS_MENUNGGU_FG   = new Color(0xD68910);
    /** Disetujui */
    public static final Color STATUS_DISETUJUI_BG  = new Color(0xEAF8EF);
    public static final Color STATUS_DISETUJUI_FG  = new Color(0x27AE60);
    /** Dibatalkan */
    public static final Color STATUS_DIBATALKAN_BG = new Color(0xF1F5F9);
    public static final Color STATUS_DIBATALKAN_FG = new Color(0x64748B);

    // ===== BADGE KATEGORI =====
    /** .kategori: bg #eef6fc / fg #3498db */
    public static final Color BADGE_KATEGORI_BG = new Color(0xEEF6FC);
    public static final Color BADGE_KATEGORI_FG = new Color(0x3498DB);

    // ===== TOMBOL =====
    /** Tombol primer (biru utama): #3498db */
    public static final Color BTN_PRIMARY_BG        = new Color(0x3498DB);
    public static final Color BTN_PRIMARY_FG        = Color.WHITE;
    public static final Color BTN_PRIMARY_HOVER_BG  = new Color(0x2980B9);

    /** Tombol outline/detail: bg white / fg brand / border brand */
    public static final Color BTN_OUTLINE_BG        = Color.WHITE;
    public static final Color BTN_OUTLINE_FG        = new Color(0x3498DB);
    public static final Color BTN_OUTLINE_BORDER    = new Color(0x3498DB);

    /** Tombol reset/filter: bg #f8fafc / fg #4b5563 / border #d1d5db */
    public static final Color BTN_RESET_BG          = new Color(0xF8FAFC);
    public static final Color BTN_RESET_FG          = new Color(0x4B5563);
    public static final Color BTN_RESET_BORDER      = new Color(0xD1D5DB);
    public static final Color BTN_RESET_HOVER_BG    = new Color(0xEFF6FF);
    public static final Color BTN_RESET_HOVER_FG    = new Color(0x3498DB);

    /** Tombol bahaya/logout: #e74c3c */
    public static final Color BTN_DANGER_BG         = new Color(0xE74C3C);
    public static final Color BTN_DANGER_FG         = Color.WHITE;

    /** border-radius tombol — rounded: 25px; normal: 8px; */
    public static final int   BTN_RADIUS_PILL   = 25;
    public static final int   BTN_RADIUS_NORMAL = 8;
    public static final int   BTN_RADIUS_LARGE  = 10;
    public static final int   BTN_HEIGHT        = 40;
    public static final int   BTN_HEIGHT_LG     = 44;

    // ===== INPUT FIELD =====
    /** .form-group input border: #e2e8f0 */
    public static final Color INPUT_BORDER        = new Color(0xE2E8F0);
    /** input:focus border: #0284c7 */
    public static final Color INPUT_BORDER_FOCUS  = new Color(0x0284C7);
    /** input:focus shadow tint */
    public static final Color INPUT_FOCUS_SHADOW  = new Color(2, 132, 199, 38);
    /** input bg */
    public static final Color INPUT_BG            = Color.WHITE;
    /** .form-group label color: #334155 */
    public static final Color INPUT_LABEL_FG      = new Color(0x334155);
    /** input placeholder: #9ca3af */
    public static final Color INPUT_PLACEHOLDER   = new Color(0x9CA3AF);
    /** input border-radius: 10px */
    public static final int   INPUT_RADIUS        = 10;
    /** input padding ~13px vertikal */
    public static final Insets INPUT_PADDING      = new Insets(10, 13, 10, 13);

    // ===== TABEL ADMIN =====
    /** .table thead th bg: #f8fafc */
    public static final Color TABLE_HEADER_BG   = new Color(0xF8FAFC);
    /** .table thead th color: #64748b */
    public static final Color TABLE_HEADER_FG   = new Color(0x64748B);
    /** .table tbody td border: #f1f5f9 */
    public static final Color TABLE_ROW_BORDER  = new Color(0xF1F5F9);
    /** .table tbody tr:hover bg: #f8fafc */
    public static final Color TABLE_ROW_HOVER   = new Color(0xF8FAFC);

    // ===== UKURAN PROFILE ICON =====
    /** .profile-icon: w=42px h=42px radius=50% bg=#3498db fg=white */
    public static final int   AVATAR_SIZE    = 42;
    public static final Color AVATAR_BG      = new Color(0x3498DB);
    public static final Color AVATAR_FG      = Color.WHITE;

    // ===== GAMBAR BARANG =====
    /** .gambar-barang height: 200px; bg: #f1f5f9 */
    public static final int   ITEM_IMG_HEIGHT = 200;
    public static final Color ITEM_IMG_BG     = new Color(0xF1F5F9);

    private ThemeConfig() { /* Utility class — tidak bisa diinstansiasi */ }

    // ===== HELPER: warna status item =====
    public static Color getStatusItemBg(String status) {
        if (status == null) return STATUS_PENDING_BG;
        return switch (status.toLowerCase()) {
            case "terbit"   -> STATUS_TERBIT_BG;
            case "selesai"  -> STATUS_SELESAI_BG;
            case "ditolak"  -> STATUS_DITOLAK_BG;
            case "arsip"    -> STATUS_ARSIP_BG;
            default         -> STATUS_PENDING_BG;
        };
    }

    public static Color getStatusItemFg(String status) {
        if (status == null) return STATUS_PENDING_FG;
        return switch (status.toLowerCase()) {
            case "terbit"   -> STATUS_TERBIT_FG;
            case "selesai"  -> STATUS_SELESAI_FG;
            case "ditolak"  -> STATUS_DITOLAK_FG;
            case "arsip"    -> STATUS_ARSIP_FG;
            default         -> STATUS_PENDING_FG;
        };
    }

    public static Color getStatusKlaimBg(String status) {
        if (status == null) return STATUS_MENUNGGU_BG;
        return switch (status.toLowerCase()) {
            case "disetujui"   -> STATUS_DISETUJUI_BG;
            case "ditolak"     -> STATUS_DITOLAK_BG;
            case "dibatalkan"  -> STATUS_DIBATALKAN_BG;
            default            -> STATUS_MENUNGGU_BG;
        };
    }

    public static Color getStatusKlaimFg(String status) {
        if (status == null) return STATUS_MENUNGGU_FG;
        return switch (status.toLowerCase()) {
            case "disetujui"   -> STATUS_DISETUJUI_FG;
            case "ditolak"     -> STATUS_DITOLAK_FG;
            case "dibatalkan"  -> STATUS_DIBATALKAN_FG;
            default            -> STATUS_MENUNGGU_FG;
        };
    }

    /** Mengatur JFrame agar selalu terbuka dalam kondisi maximized (layar penuh dengan taskbar tetap terlihat). */
    public static void applyMaximized(javax.swing.JFrame frame) {
        if (frame != null) {
            frame.setExtendedState(frame.getExtendedState() | javax.swing.JFrame.MAXIMIZED_BOTH);
        }
    }

    /** Panel dengan gradien navy gelap 3-stop yang seragam dengan panel kiri LoginFrame (#1e293b -> #0f172a -> #1e3a8a). */
    public static javax.swing.JPanel createHeroGradientPanel() {
        return new javax.swing.JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                LinearGradientPaint gp = new LinearGradientPaint(
                        0, 0, Math.max(1, w), Math.max(1, h),
                        new float[]{0f, 0.45f, 1f},
                        new Color[]{HERO_GRAD_START, HERO_GRAD_MID, HERO_GRAD_END}
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);
                g2.dispose();
            }
        };
    }

    /** Mengembalikan Window induk (JFrame/JDialog) agar dialog selalu tampil tepat di tengah jendela utama. */
    public static java.awt.Window windowOf(java.awt.Component comp) {
        if (comp == null) return null;
        if (comp instanceof java.awt.Window w) return w;
        return javax.swing.SwingUtilities.getWindowAncestor(comp);
    }
}
