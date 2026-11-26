package com.foundit.view.components;

import com.foundit.view.ThemeConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * AvatarLabel — lingkaran inisial pengguna.
 *
 * Meniru .profile-icon dari CSS web:
 *   width:42px; height:42px; border-radius:50%;
 *   background:#3498db; color:white; font-weight:bold;
 */
public class AvatarLabel extends JLabel {

    private final int size;

    public AvatarLabel(String inisial) {
        this(inisial, ThemeConfig.AVATAR_SIZE);
    }

    public AvatarLabel(String inisial, int size) {
        super(inisial, SwingConstants.CENTER);
        this.size = size;
        setOpaque(false);
        setFont(ThemeConfig.FONT_BOLD);
        setForeground(ThemeConfig.AVATAR_FG);
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(ThemeConfig.AVATAR_BG);
        g2.fill(new Ellipse2D.Double(0, 0, size, size));
        g2.dispose();
        super.paintComponent(g);
    }

    /** Update inisial saat user berganti / login */
    public void setInisial(String inisial) {
        setText(inisial != null && !inisial.isEmpty()
                ? String.valueOf(Character.toUpperCase(inisial.charAt(0)))
                : "?");
        repaint();
    }
}
