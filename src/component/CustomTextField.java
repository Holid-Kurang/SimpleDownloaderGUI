package component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Custom JTextField dengan styling modern Fitur: - Rounded corners - Border
 * styling dengan focus effect - Placeholder text - Smooth focus transitions -
 * Padding yang nyaman
 */
public class CustomTextField extends JTextField {

    private String placeholder = "";
    private Color borderColor = new Color(199, 210, 254); // indigo-200
    private Color focusBorderColor = new Color(99, 102, 241); // indigo-500
    private Color focusRingColor = new Color(99, 102, 241, 50); // indigo-500 with alpha
    private Color placeholderColor = new Color(209, 213, 219); // gray-300

    private boolean isFocused = false;
    private int cornerRadius = 20;
    private int borderWidth = 2;
    private int padding = 12;

    // For smooth transition animation
    private float focusAmount = 0f;
    private Timer focusTimer;

    public CustomTextField() {
        this("");
    }

    public CustomTextField(String placeholder) {
        this(placeholder, 20);
    }

    public CustomTextField(String placeholder, int columns) {
        super(columns);
        this.placeholder = placeholder;
        initialize();
    }

    private void initialize() {
        setOpaque(false);
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setForeground(new Color(31, 41, 55)); // gray-800
        setCaretColor(new Color(99, 102, 241)); // indigo-500

        // Set padding using empty border
        setBorder(new EmptyBorder(padding, padding + 4, padding, padding + 4));

        // Focus listener for animation
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                startFocusAnimation(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                startFocusAnimation(false);
            }
        });
    }

    private void startFocusAnimation(boolean focusing) {
        if (focusTimer != null && focusTimer.isRunning()) {
            focusTimer.stop();
        }

        focusTimer = new Timer(20, e -> {
            if (focusing) {
                focusAmount = Math.min(1f, focusAmount + 0.15f);
            } else {
                focusAmount = Math.max(0f, focusAmount - 0.15f);
            }

            if ((focusing && focusAmount >= 1f) || (!focusing && focusAmount <= 0f)) {
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
        focusTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int width = getWidth();
        int height = getHeight();

        // Draw background
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

        // Draw focus ring (shadow effect when focused)
        if (focusAmount > 0) {
            g2.setColor(new Color(
                    focusRingColor.getRed(),
                    focusRingColor.getGreen(),
                    focusRingColor.getBlue(),
                    (int) (focusRingColor.getAlpha() * focusAmount)
            ));
        }

        g2.dispose();

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Interpolate border color based on focus
        Color currentBorderColor = interpolateColor(borderColor, focusBorderColor, focusAmount);

        g2.setColor(currentBorderColor);
        g2.setStroke(new BasicStroke(borderWidth));

        RoundRectangle2D border = new RoundRectangle2D.Float(
                borderWidth / 2f, borderWidth / 2f,
                width - borderWidth, height - borderWidth,
                cornerRadius, cornerRadius
        );
        g2.draw(border);

        g2.dispose();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // Draw placeholder if text is empty and not focused
        if (getText().isEmpty() && !isFocused) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(placeholderColor);
            g2.setFont(getFont());

            FontMetrics fm = g2.getFontMetrics();
            int x = getInsets().left;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

            g2.drawString(placeholder, x, y);
            g2.dispose();
        }
    }

    private Color interpolateColor(Color c1, Color c2, float ratio) {
        ratio = Math.max(0f, Math.min(1f, ratio));
        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * ratio);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * ratio);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * ratio);
        return new Color(r, g, b);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.height = Math.max(size.height, 46); // Minimum height for better UX
        return size;
    }

    // ===== Getter and Setter Methods =====
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setFocusBorderColor(Color color) {
        this.focusBorderColor = color;
        repaint();
    }

    public Color getFocusBorderColor() {
        return focusBorderColor;
    }

    public void setFocusRingColor(Color color) {
        this.focusRingColor = color;
        repaint();
    }

    public Color getFocusRingColor() {
        return focusRingColor;
    }

    public void setPlaceholderColor(Color color) {
        this.placeholderColor = color;
        repaint();
    }

    public Color getPlaceholderColor() {
        return placeholderColor;
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setBorderWidth(int width) {
        this.borderWidth = width;
        repaint();
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setPadding(int padding) {
        this.padding = padding;
        setBorder(new EmptyBorder(padding, padding + 4, padding, padding + 4));
        repaint();
    }

    public int getPadding() {
        return padding;
    }
}
