package com.foundit.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Komponen visual bersama dengan palet yang mengikuti desain FoundIt[cite: 68].
 */
public final class UiKit {

    public static final Color BLUE = new Color(52, 152, 219);
    public static final Color BLUE_DARK = new Color(44, 62, 80);
    public static final Color BLUE_HOVER = new Color(41, 128, 185);

    public static final Color SKY = new Color(169, 231, 255);
    public static final Color BG = new Color(247, 249, 252);
    public static final Color PALE = new Color(234, 245, 252);

    public static final Color TEXT = new Color(44, 62, 80);
    public static final Color MUTED = new Color(119, 136, 153);
    public static final Color GREEN = new Color(46, 204, 113);
    public static final Color BORDER = new Color(218, 228, 237);

    public static final Font REG = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font MED = new Font("Segoe UI", Font.BOLD, 14);

    private UiKit() {
    }

    public static void frame(JFrame f, String title, int w, int h) {
        f.setTitle(title);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(w, h);
        f.setMinimumSize(new Dimension(1050, 680));
        f.setLocationRelativeTo(null);
        ThemeConfig.applyMaximized(f);
    }

    public static JPanel gradient(Color a, Color b) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2.setPaint(new GradientPaint(0, 0, a, getWidth(), getHeight(), b));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
    }

    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(231, 237, 243)),
                        new EmptyBorder(20, 22, 20, 22)
                )
        );
        return p;
    }

    public static JButton button(String text, Color bg, Color fg) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2.setColor(
                        getModel().isPressed()
                                ? bg.darker()
                                : getModel().isRollover()
                                        ? bg.brighter()
                                        : bg
                );
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        b.setFont(MED);
        b.setForeground(fg);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorder(new EmptyBorder(0, 20, 0, 20));
        b.setMargin(new Insets(0, 20, 0, 20));
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setVerticalAlignment(SwingConstants.CENTER);
        b.setVerticalTextPosition(SwingConstants.CENTER);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JLabel label(String s, int size, boolean bold, Color c) {
        JLabel l = new JLabel(s);
        l.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, size));
        l.setForeground(c);
        return l;
    }

    public static JTextField field() {
        JTextField f = new JTextField();
        f.setFont(REG);
        f.setForeground(TEXT);
        f.setBackground(new Color(250, 252, 254));
        f.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(190, 207, 222), 1, true),
                        new EmptyBorder(10, 12, 10, 12)
                )
        );
        f.setPreferredSize(new Dimension(220, 42));
        return f;
    }

    public static JPasswordField password() {
        JPasswordField f = new JPasswordField();
        f.setFont(REG);
        f.setForeground(TEXT);
        f.setBackground(new Color(250, 252, 254));
        f.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(190, 207, 222), 1, true),
                        new EmptyBorder(10, 12, 10, 12)
                )
        );
        f.setPreferredSize(new Dimension(220, 42));
        return f;
    }

    public static JPanel fieldBlock(String title, JComponent input) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel caption = label(title, 13, true, TEXT);
        caption.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(caption);
        p.add(Box.createVerticalStrut(7));

        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        input.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(input);
        return p;
    }

    public static Icon icon(String kind, Color color, int size) {
        return new Icon() {
            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }

            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D a = (Graphics2D) g.create();
                a.translate(x, y);
                a.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                a.setColor(color);
                a.setStroke(
                        new BasicStroke(
                                Math.max(1.8f, size / 13f),
                                BasicStroke.CAP_ROUND,
                                BasicStroke.JOIN_ROUND
                        )
                );

                int m = size / 5;
                int r = size * 3 / 5;

                switch (kind) {
                    case "search" -> {
                        a.drawOval(m, m, r, r);
                        a.drawLine(size * 3 / 4, size * 3 / 4, size - 2, size - 2);
                    }
                    case "report" -> {
                        a.drawRoundRect(m, m, size - 2 * m, size - 2 * m, 5, 5);
                        a.drawLine(size / 3, size / 2, size * 2 / 3, size / 2);
                        a.drawLine(size / 3, size * 2 / 3, size * 2 / 3, size * 2 / 3);
                        a.drawLine(size / 3, size / 3, size / 2, size / 3);
                    }
                    case "return" -> {
                        java.awt.geom.Path2D heart = new java.awt.geom.Path2D.Double();
                        heart.moveTo(size / 2.0, size * 0.82);
                        heart.curveTo(size * 0.38, size * 0.70, size * 0.12, size * 0.50, size * 0.16, size * 0.31);
                        heart.curveTo(size * 0.19, size * 0.10, size * 0.43, size * 0.12, size / 2.0, size * 0.34);
                        heart.curveTo(size * 0.57, size * 0.12, size * 0.81, size * 0.10, size * 0.84, size * 0.31);
                        heart.curveTo(size * 0.88, size * 0.50, size * 0.62, size * 0.70, size / 2.0, size * 0.82);
                        a.draw(heart);
                    }
                    case "home" -> {
                        a.drawLine(3, size / 2, size / 2, 3);
                        a.drawLine(size / 2, 3, size - 3, size / 2);
                        a.drawRect(size / 5, size / 2, size * 3 / 5, size * 2 / 5);
                        a.drawRect(size * 2 / 5, size * 2 / 3, size / 5, size / 3);
                    }
                    case "lost" -> {
                        a.drawOval(size / 4, size / 8, size / 2, size / 2);
                        a.drawArc(size / 6, size / 2, size * 2 / 3, size / 2, 0, 180);
                    }
                    case "found" -> {
                        a.drawRoundRect(size / 5, size / 5, size * 3 / 5, size * 3 / 5, 5, 5);
                        a.drawLine(size / 2, size / 3, size / 2, size * 2 / 3);
                        a.drawLine(size / 3, size / 2, size * 2 / 3, size / 2);
                    }
                    default -> a.drawRoundRect(m, m, size - 2 * m, size - 2 * m, 5, 5);
                }

                a.dispose();
            }
        };
    }

    public static JPanel header(String title, String subtitle) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(label(title, 27, true, TEXT));
        p.add(Box.createVerticalStrut(5));
        p.add(label(subtitle, 13, false, MUTED));
        return p;
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "FoundIt", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void warn(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "FoundIt - Perhatian", JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "FoundIt - Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(
                parent, message, "FoundIt - Konfirmasi",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
        ) == JOptionPane.YES_OPTION;
    }
}