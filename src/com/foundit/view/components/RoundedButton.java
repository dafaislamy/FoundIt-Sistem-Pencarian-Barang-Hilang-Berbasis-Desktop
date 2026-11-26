package com.foundit.view.components;

import com.foundit.view.ThemeConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * RoundedButton — tombol kustom dengan sudut melengkung dan efek hover.
 *
 * Meniru gaya CSS tombol FoundIt:
 *   .btn-login      { bg:#0284c7; color:white; border-radius:10px; h:44px; }
 *   .btn-card-klaim { bg:#3498db; color:white; border-radius:8px;  h:40px; }
 *   .btn-detail     { bg:white;   color:#3498db; border:1px solid #3498db; border-radius:8px; }
 *   .btn-utama      { border-radius:30px (pill); }
 */
public class RoundedButton extends JButton {

    public enum Variant { PRIMARY, OUTLINE, DANGER, RESET }

    private int     arcRadius;
    private Color   normalBg;
    private Color   normalFg;
    private Color   hoverBg;
    private Color   hoverFg;
    private Color   borderColor;
    private boolean isHovered = false;

    /** Tombol primer (biru utama, border-radius 8px) */
    public RoundedButton(String text) {
        this(text, Variant.PRIMARY, ThemeConfig.BTN_RADIUS_NORMAL);
    }

    public RoundedButton(String text, Variant variant, int arcRadius) {
        super(text);
        this.arcRadius = arcRadius;

        switch (variant) {
            case OUTLINE -> {
                normalBg    = ThemeConfig.BTN_OUTLINE_BG;
                normalFg    = ThemeConfig.BTN_OUTLINE_FG;
                hoverBg     = ThemeConfig.BTN_PRIMARY_BG;
                hoverFg     = Color.WHITE;
                borderColor = ThemeConfig.BTN_OUTLINE_BORDER;
            }
            case DANGER -> {
                normalBg    = ThemeConfig.BTN_DANGER_BG;
                normalFg    = ThemeConfig.BTN_DANGER_FG;
                hoverBg     = new Color(0xC0392B);
                hoverFg     = Color.WHITE;
                borderColor = null;
            }
            case RESET -> {
                normalBg    = ThemeConfig.BTN_RESET_BG;
                normalFg    = ThemeConfig.BTN_RESET_FG;
                hoverBg     = ThemeConfig.BTN_RESET_HOVER_BG;
                hoverFg     = ThemeConfig.BTN_RESET_HOVER_FG;
                borderColor = ThemeConfig.BTN_RESET_BORDER;
            }
            default -> { // PRIMARY
                normalBg    = ThemeConfig.BTN_PRIMARY_BG;
                normalFg    = ThemeConfig.BTN_PRIMARY_FG;
                hoverBg     = ThemeConfig.BTN_PRIMARY_HOVER_BG;
                hoverFg     = Color.WHITE;
                borderColor = null;
            }
        }

        applyBase();

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { isHovered = true;  repaint(); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { isHovered = false; repaint(); }
        });
    }

    private void applyBase() {
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setFont(ThemeConfig.FONT_SEMIBOLD);
        setPreferredSize(new Dimension(getPreferredSize().width, ThemeConfig.BTN_HEIGHT));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bg = isHovered ? hoverBg : normalBg;
        Color fg = isHovered ? hoverFg : normalFg;

        // Latar belakang tombol
        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arcRadius, arcRadius));

        // Border (jika ada)
        if (borderColor != null) {
            g2.setColor(isHovered ? (hoverBg.equals(ThemeConfig.BTN_PRIMARY_BG) ? ThemeConfig.BTN_PRIMARY_BG : borderColor) : borderColor);
            g2.setStroke(new BasicStroke(1));
            g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth()-1, getHeight()-1, arcRadius, arcRadius));
        }

        g2.dispose();

        // Render teks dengan warna yang sesuai
        setForeground(fg);
        super.paintComponent(g);
    }

    // ===== Factory Methods =====

    /** .btn-login — tombol login besar, sudut 10px, tinggi 44px */
    public static RoundedButton createLoginButton(String text) {
        RoundedButton b = new RoundedButton(text, Variant.PRIMARY, ThemeConfig.BTN_RADIUS_LARGE);
        b.normalBg = ThemeConfig.COLOR_BRAND_DARK;
        b.hoverBg  = ThemeConfig.COLOR_BRAND_DARKER;
        b.setPreferredSize(new Dimension(Integer.MAX_VALUE, ThemeConfig.BTN_HEIGHT_LG));
        b.setFont(ThemeConfig.FONT_BOLD);
        return b;
    }

    /** .btn-register — tombol register, sudut 7px */
    public static RoundedButton createRegisterButton(String text) {
        RoundedButton b = new RoundedButton(text, Variant.PRIMARY, 7);
        b.setPreferredSize(new Dimension(Integer.MAX_VALUE, ThemeConfig.BTN_HEIGHT_LG));
        b.setFont(ThemeConfig.FONT_BOLD);
        return b;
    }

    /** .btn-utama — tombol hero bergaya pill (sudut 30px) */
    public static RoundedButton createPillButton(String text) {
        return new RoundedButton(text, Variant.PRIMARY, ThemeConfig.BTN_RADIUS_PILL);
    }

    /** .btn-detail — tombol outline (border biru, bg putih, hover biru) */
    public static RoundedButton createOutlineButton(String text) {
        return new RoundedButton(text, Variant.OUTLINE, ThemeConfig.BTN_RADIUS_NORMAL);
    }

    /** .logout — tombol merah */
    public static RoundedButton createDangerButton(String text) {
        return new RoundedButton(text, Variant.DANGER, ThemeConfig.BTN_RADIUS_NORMAL);
    }
}
