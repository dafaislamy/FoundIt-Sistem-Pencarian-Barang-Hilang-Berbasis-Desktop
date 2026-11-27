package com.foundit.view.components;

import com.foundit.view.ThemeConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * RoundedPanel — panel dengan sudut melengkung dan opsional bayangan/border.
 *
 * Meniru gaya CSS:
 *   .stat-card   { border-radius: 12px; border: 1px solid #e5e7eb; background: white; }
 *   .fitur-box   { border-radius: 18px; box-shadow: 0 10px 30px rgba(15,23,42,0.07); }
 *   .action-card { border-radius: 12px; border: 1px solid #e5e7eb; }
 */
public class RoundedPanel extends JPanel {

    private int   arcRadius;
    private Color borderColor;
    private int   borderThickness;
    private boolean dropShadow;
    private Color gradientStart;
    private Color gradientEnd;

    /** Panel putih bertepi melengkung standar (12px), border abu #e5e7eb */
    public RoundedPanel() {
        this(ThemeConfig.CARD_RADIUS, ThemeConfig.BORDER_CARD, 1, false);
    }

    public RoundedPanel(int arcRadius) {
        this(arcRadius, ThemeConfig.BORDER_CARD, 1, false);
    }

    public RoundedPanel(int arcRadius, Color borderColor, int borderThickness, boolean dropShadow) {
        this.arcRadius       = arcRadius;
        this.borderColor     = borderColor;
        this.borderThickness = borderThickness;
        this.dropShadow      = dropShadow;
        setOpaque(false);
        setBackground(ThemeConfig.BG_CARD);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int shadow = dropShadow ? 4 : 0;
        int w = getWidth()  - shadow;
        int h = getHeight() - shadow;

        // Bayangan lembut (simulasi box-shadow)
        if (dropShadow) {
            g2.setColor(new Color(15, 23, 42, 20));
            g2.fill(new RoundRectangle2D.Double(shadow, shadow + 2, w, h, arcRadius, arcRadius));
        }

        // Latar belakang (gradien jika diatur, atau solid background)
        if (gradientStart != null && gradientEnd != null) {
            g2.setPaint(new GradientPaint(0, 0, gradientStart, w, h, gradientEnd));
        } else {
            g2.setColor(getBackground());
        }
        g2.fill(new RoundRectangle2D.Double(0, 0, w, h, arcRadius, arcRadius));

        // Border
        if (borderColor != null && borderThickness > 0) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(borderThickness));
            g2.draw(new RoundRectangle2D.Double(
                    (double) borderThickness / 2,
                    (double) borderThickness / 2,
                    w - borderThickness,
                    h - borderThickness,
                    arcRadius, arcRadius));
        }

        g2.dispose();
        super.paintComponent(g);
    }

    // ===== Setter fluent =====
    public RoundedPanel setArcRadius(int r)              { arcRadius = r;        repaint(); return this; }
    public RoundedPanel setBorderColor(Color c)          { borderColor = c;      repaint(); return this; }
    public RoundedPanel setBorderThickness(int t)        { borderThickness = t;  repaint(); return this; }
    public RoundedPanel setDropShadow(boolean s)         { dropShadow = s;       repaint(); return this; }

    /** Factory: kartu statistik standar */
    public static RoundedPanel createStatCard() {
        RoundedPanel p = new RoundedPanel(ThemeConfig.CARD_RADIUS, ThemeConfig.BORDER_CARD, 1, false);
        p.setBackground(ThemeConfig.BG_CARD);
        p.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        return p;
    }

    /** Factory: kartu fitur besar dengan shadow */
    public static RoundedPanel createFeatureCard() {
        RoundedPanel p = new RoundedPanel(ThemeConfig.CARD_RADIUS_LG, ThemeConfig.BORDER_CARD, 1, true);
        p.setBackground(ThemeConfig.BG_CARD);
        return p;
    }

    /** Factory: welcome card gradient biru gelap/navy */
    public static RoundedPanel createWelcomeCard() {
        RoundedPanel p = new RoundedPanel(14, null, 0, false);
        p.gradientStart = ThemeConfig.WELCOME_GRAD_START;
        p.gradientEnd = ThemeConfig.WELCOME_GRAD_END;
        return p;
    }
}
