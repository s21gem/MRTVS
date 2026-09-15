package Classes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class UIHelper {
    // Elegant color palette
    public static final Color COLOR_PRIMARY = new Color(16, 185, 129); // Emerald Green
    public static final Color COLOR_PRIMARY_HOVER = new Color(5, 150, 105);
    public static final Color COLOR_ACCENT = new Color(59, 130, 246); // Royal Blue
    public static final Color COLOR_ACCENT_HOVER = new Color(29, 78, 216);
    public static final Color COLOR_DANGER = new Color(239, 68, 68); // Coral Red
    public static final Color COLOR_DANGER_HOVER = new Color(220, 38, 38);
    
    public static final Color COLOR_BG_DARK = new Color(15, 23, 42); // Slate 900
    public static final Color COLOR_BG_PANEL = new Color(30, 41, 59); // Slate 800
    public static final Color COLOR_TEXT_LIGHT = new Color(241, 245, 249); // Slate 100
    public static final Color COLOR_TEXT_MUTED = new Color(148, 163, 184); // Slate 400
    public static final Color COLOR_INPUT_BG = new Color(51, 65, 85); // Slate 700

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 36);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 15);

    // Custom Rounded Panel
    public static class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color backgroundColor;

        public RoundedPanel(int radius, Color bg) {
            super();
            this.cornerRadius = radius;
            this.backgroundColor = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (backgroundColor != null) {
                graphics.setColor(backgroundColor);
            } else {
                graphics.setColor(getBackground());
            }
            graphics.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

            // Draw glass border highlight (thin white semi-transparent outline)
            graphics.setColor(new Color(255, 255, 255, 30));
            graphics.setStroke(new BasicStroke(1.5f));
            graphics.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }
    }

    // Custom Gradient Panel
    public static class GradientPanel extends JPanel {
        private Color startColor;
        private Color endColor;

        public GradientPanel(Color startColor, Color endColor) {
            this.startColor = startColor;
            this.endColor = endColor;
            setLayout(null);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // Custom Image Panel (scales and paints background image)
    public static class ImagePanel extends JPanel {
        private Image backgroundImage;

        public ImagePanel(String imagePath) {
            super();
            try {
                this.backgroundImage = new ImageIcon(imagePath).getImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setLayout(null);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    // Custom Flat Rounded Button
    public static class ModernButton extends JButton {
        private Color baseColor = COLOR_PRIMARY;
        private Color hoverColor = COLOR_PRIMARY_HOVER;
        private boolean isHovered = false;

        public ModernButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(FONT_BODY_BOLD);
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        public ModernButton(String text, Color baseColor, Color hoverColor) {
            this(text);
            this.baseColor = baseColor;
            this.hoverColor = hoverColor;
        }

        public void setColors(Color baseColor, Color hoverColor) {
            this.baseColor = baseColor;
            this.hoverColor = hoverColor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (isEnabled()) {
                g2d.setColor(isHovered ? hoverColor : baseColor);
            } else {
                g2d.setColor(COLOR_INPUT_BG);
            }
            
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            
            super.paintComponent(g);
        }
    }

    // Custom Text Field
    public static class ModernTextField extends JTextField implements FocusListener {
        private String placeholder;
        private boolean isPlaceholderActive = true;

        public ModernTextField(String placeholder) {
            super(placeholder);
            this.placeholder = placeholder;
            setForeground(COLOR_TEXT_MUTED);
            setBackground(COLOR_INPUT_BG);
            setCaretColor(Color.WHITE);
            setFont(FONT_BODY);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_INPUT_BG, 1, true),
                    new EmptyBorder(5, 10, 5, 10)
            ));
            addFocusListener(this);
        }

        @Override
        public String getText() {
            return isPlaceholderActive ? "" : super.getText();
        }

        public void setCleanText(String text) {
            isPlaceholderActive = false;
            setForeground(COLOR_TEXT_LIGHT);
            super.setText(text);
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (isPlaceholderActive) {
                isPlaceholderActive = false;
                setText("");
                setForeground(COLOR_TEXT_LIGHT);
            }
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_PRIMARY, 1, true),
                    new EmptyBorder(5, 10, 5, 10)
            ));
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (super.getText().isEmpty()) {
                isPlaceholderActive = true;
                setText(placeholder);
                setForeground(COLOR_TEXT_MUTED);
            }
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_INPUT_BG, 1, true),
                    new EmptyBorder(5, 10, 5, 10)
            ));
        }
    }

    // Custom Password Field
    public static class ModernPasswordField extends JPasswordField implements FocusListener {
        private String placeholder;
        private boolean isPlaceholderActive = true;
        private char echoChar;

        public ModernPasswordField(String placeholder) {
            super(placeholder);
            this.placeholder = placeholder;
            this.echoChar = getEchoChar();
            setEchoChar((char) 0); // Show placeholder plain text
            setForeground(COLOR_TEXT_MUTED);
            setBackground(COLOR_INPUT_BG);
            setCaretColor(Color.WHITE);
            setFont(FONT_BODY);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_INPUT_BG, 1, true),
                    new EmptyBorder(5, 10, 5, 10)
            ));
            addFocusListener(this);
        }

        @Override
        public char[] getPassword() {
            return isPlaceholderActive ? new char[0] : super.getPassword();
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (isPlaceholderActive) {
                isPlaceholderActive = false;
                setText("");
                setEchoChar(echoChar);
                setForeground(COLOR_TEXT_LIGHT);
            }
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_PRIMARY, 1, true),
                    new EmptyBorder(5, 10, 5, 10)
            ));
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (new String(super.getPassword()).isEmpty()) {
                isPlaceholderActive = true;
                setEchoChar((char) 0);
                setText(placeholder);
                setForeground(COLOR_TEXT_MUTED);
            }
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_INPUT_BG, 1, true),
                    new EmptyBorder(5, 10, 5, 10)
            ));
        }
    }
}
