package com.foundit.view.components;

import com.foundit.view.ThemeConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * StatusBadge — label badge berpojok melengkung untuk menampilkan:
 *   - Tipe barang  : "Barang Hilang" / "Barang Ditemukan"
 *   - Status item  : Pending, Terbit, Selesai, Ditolak, Arsip
 *   - Status klaim : Menunggu, Disetujui, Ditolak, Dibatalkan
 *   - Kategori     : nama kategori dalam chip biru muda
 *
 * Meniru .badge / .hilang / .ditemukan / .status-badge / .kategori dari web.
 */
public class StatusBadge extends JLabel {

    private Color bgColor;
    private Color fgColor;
    private int   arcRadius;

    public StatusBadge(String text, Color bgColor, Color fgColor) {
        this(text, bgColor, fgColor, 20);
    }

    public StatusBadge(String text, Color bgColor, Color fgColor, int arcRadius) {
        super(text, SwingConstants.CENTER);
        this.bgColor   = bgColor;
        this.fgColor   = fgColor;
        this.arcRadius = arcRadius;
        setFont(ThemeConfig.FONT_BADGE);
        setForeground(fgColor);
        setOpaque(false);
        // padding horizontal ditangani lewat border
        setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bgColor);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arcRadius, arcRadius));
        g2.dispose();
        super.paintComponent(g);
    }

    // ===== Factory Methods =====

    /** Badge tipe barang "Barang Hilang" → merah (#ffebee / #c62828) */
    public static StatusBadge lost() {
        return new StatusBadge("Barang Hilang", ThemeConfig.BADGE_LOST_BG, ThemeConfig.BADGE_LOST_FG);
    }

    /** Badge tipe barang "Barang Ditemukan" → hijau (#e8f5e9 / #2e7d32) */
    public static StatusBadge found() {
        return new StatusBadge("Barang Ditemukan", ThemeConfig.BADGE_FOUND_BG, ThemeConfig.BADGE_FOUND_FG);
    }

    /** Badge tipe barang berdasarkan string tipe dari DB ("lost"/"found") */
    public static StatusBadge forType(String tipe) {
        if ("lost".equalsIgnoreCase(tipe)) return lost();
        return found();
    }

    /** Badge status item (Pending, Terbit, Selesai, Ditolak, Arsip) */
    public static StatusBadge forItemStatus(String status) {
        Color bg = ThemeConfig.getStatusItemBg(status);
        Color fg = ThemeConfig.getStatusItemFg(status);
        String label = (status != null) ? capitalize(status) : "Pending";
        return new StatusBadge(label, bg, fg);
    }

    /** Badge status klaim (Menunggu, Disetujui, Ditolak, Dibatalkan) */
    public static StatusBadge forKlaimStatus(String status) {
        Color bg = ThemeConfig.getStatusKlaimBg(status);
        Color fg = ThemeConfig.getStatusKlaimFg(status);
        String label = (status != null) ? capitalize(status) : "Menunggu";
        return new StatusBadge(label, bg, fg);
    }

    /** Badge kategori → .kategori: bg #eef6fc / fg #3498db, border-radius 5px */
    public static StatusBadge forKategori(String namaKategori) {
        return new StatusBadge(namaKategori,
                ThemeConfig.BADGE_KATEGORI_BG,
                ThemeConfig.BADGE_KATEGORI_FG, 5);
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }
}
