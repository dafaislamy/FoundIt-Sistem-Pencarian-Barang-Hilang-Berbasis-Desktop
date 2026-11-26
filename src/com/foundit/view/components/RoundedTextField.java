package com.foundit.view.components;

import com.foundit.view.ThemeConfig;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * RoundedTextField — input field bertepi melengkung dengan efek focus glow.
 *
 * Meniru .form-group input dari CSS web:
 *   border: 1.5px solid #e2e8f0; border-radius: 10px; padding: 13px;
 *   :focus { border-color: #0284c7; box-shadow: 0 0 0 3px rgba(2,132,199,0.15) }
 */
public class RoundedTextField extends JTextField {

    private Color borderColor = ThemeConfig.INPUT_BORDER;
    private final int radius;

    public RoundedTextField() {
        this(ThemeConfig.INPUT_RADIUS);
    }

    public RoundedTextField(int radius) {
        this.radius = radius;
        setOpaque(false);
        setBackground(ThemeConfig.INPUT_BG);
        setFont(ThemeConfig.FONT_MEDIUM);
        setForeground(ThemeConfig.COLOR_TEXT_PRIMARY);
        setBorder(new RoundedFieldBorder(radius));

        addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                borderColor = ThemeConfig.INPUT_BORDER_FOCUS;
                repaint();
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                borderColor = ThemeConfig.INPUT_BORDER;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Glow saat focus
        if (isFocusOwner()) {
            g2.setColor(ThemeConfig.INPUT_FOCUS_SHADOW);
            g2.fill(new RoundRectangle2D.Double(-3, -3, getWidth()+6, getHeight()+6, radius+4, radius+4));
        }
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(isFocusOwner() ? 1.5f : 1.5f));
        g2.draw(new RoundRectangle2D.Double(0.75, 0.75, getWidth()-1.5, getHeight()-1.5, radius, radius));
        g2.dispose();
    }

    private static class RoundedFieldBorder extends AbstractBorder {
        private final int r;
        RoundedFieldBorder(int r) { this.r = r; }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(ThemeConfig.INPUT_PADDING.top,
                    ThemeConfig.INPUT_PADDING.left,
                    ThemeConfig.INPUT_PADDING.bottom,
                    ThemeConfig.INPUT_PADDING.right);
        }
    }
}
