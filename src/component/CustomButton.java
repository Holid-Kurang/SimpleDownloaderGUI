package component;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * Advanced Custom Button dengan animasi dan efek modern Fitur: - Ripple effect
 * saat klik - Smooth color transitions - Shadow effect - Icon support dengan
 * spacing
 */
public class CustomButton extends JButton {

    private Color backgroundColor = new Color(25, 118, 210);
    private Color hoverColor = new Color(30, 136, 229);
    private Color rippleColor = new Color(255, 255, 255, 100);

    private boolean isHovered = false;
    private boolean isPressed = false;

    private float hoverAmount = 0f;
    private Timer hoverTimer;

    // Ripple effect variables
    private Point rippleCenter;
    private float rippleRadius = 0f;
    private Timer rippleTimer;
    private boolean rippleActive = false;

    // Shadow
    private boolean drawShadow = true;
    private int shadowSize = 4;

    // Rounded corners
    private int cornerRadius = 20;

    // Gradient
    private boolean useGradient = false;

    // Border
    private boolean drawBorder = false;
    private float borderWidth = 2f;
    private Color borderColor = null; // null = auto (darker from background)

    public CustomButton(String text) {
        super(text);
        initialize();
    }

    public CustomButton(String text, Icon icon) {
        super(text, icon);
        setIconTextGap(10);
        initialize();
    }

    private void initialize() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover animation timer
        hoverTimer = new Timer(20, e -> {
            repaint();
        });

        // Ripple animation timer
        rippleTimer = new Timer(15, e -> {
            if (rippleActive) {
                rippleRadius += 5f;
                if (rippleRadius > Math.max(getWidth(), getHeight()) * 1.5f) {
                    rippleActive = false;
                    rippleRadius = 0f;
                }
                repaint();
            } else {
                ((Timer) e.getSource()).stop();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                startHoverAnimation(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                startHoverAnimation(false);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                if (isEnabled()) {
                    startRipple(e.getPoint());
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    private void startHoverAnimation(boolean entering) {
        hoverTimer.stop();
        hoverTimer = new Timer(20, e -> {
            if (entering) {
                hoverAmount = Math.min(1f, hoverAmount + 0.1f);
            } else {
                hoverAmount = Math.max(0f, hoverAmount - 0.1f);
            }

            if ((entering && hoverAmount >= 1f) || (!entering && hoverAmount <= 0f)) {
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
        hoverTimer.start();
    }

    private void startRipple(Point center) {
        rippleCenter = center;
        rippleRadius = 0f;
        rippleActive = true;
        rippleTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int width = getWidth();
        int height = getHeight();
        int arc = cornerRadius;

        // Draw shadow
        if (drawShadow && isEnabled()) {
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(shadowSize, shadowSize, width - shadowSize, height - shadowSize, arc, arc);
        }

        // Interpolate color based on hover amount
        Color currentColor = interpolateColor(backgroundColor, hoverColor, hoverAmount);

        if (!isEnabled()) {
            currentColor = new Color(180, 180, 180);
        }

        // Draw background with gradient
        if (useGradient && isEnabled()) {
            GradientPaint gradient = new GradientPaint(
                    0, 0, currentColor,
                    0, height, darken(currentColor, 0.85f)
            );
            g2.setPaint(gradient);
        } else {
            g2.setColor(currentColor);
        }

        RoundRectangle2D buttonShape = new RoundRectangle2D.Float(
                0, 0, width , height , arc, arc
        );
        g2.fill(buttonShape);

        // Draw ripple effect
        if (rippleActive && rippleCenter != null && isEnabled()) {
            g2.setClip(buttonShape);
            g2.setColor(rippleColor);
            g2.fillOval(
                    (int) (rippleCenter.x - rippleRadius),
                    (int) (rippleCenter.y - rippleRadius),
                    (int) (rippleRadius * 2),
                    (int) (rippleRadius * 2)
            );
            g2.setClip(null);
        }

        // Draw border
        if (drawBorder) {
            Color border = borderColor != null ? borderColor : darken(currentColor, 0.7f);
            g2.setColor(border);
            g2.setStroke(new BasicStroke(borderWidth));
            g2.draw(buttonShape);
        }

        g2.dispose();

        super.paintComponent(g);
    }

    private Color interpolateColor(Color c1, Color c2, float ratio) {
        ratio = Math.max(0f, Math.min(1f, ratio));
        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * ratio);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * ratio);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * ratio);
        return new Color(r, g, b);
    }

    private Color darken(Color color, float factor) {
        return new Color(
                Math.max((int) (color.getRed() * factor), 0),
                Math.max((int) (color.getGreen() * factor), 0),
                Math.max((int) (color.getBlue() * factor), 0)
        );
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.width += 16;
        size.height += 8;
        return size;
    }

    // ===== Getter and Setter Methods =====
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        this.hoverColor = color.darker();
        repaint();
    }

    public void setHoverColor(Color color) {
        this.hoverColor = color;
        repaint();
    }

    public void setRippleColor(Color color) {
        this.rippleColor = color;
        repaint();
    }

    public void setDrawShadow(boolean drawShadow) {
        this.drawShadow = drawShadow;
        repaint();
    }

    public void setShadowSize(int shadowSize) {
        this.shadowSize = shadowSize;
        repaint();
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getHoverColor() {
        return hoverColor;
    }

    public Color getRippleColor() {
        return rippleColor;
    }

    public boolean isDrawShadow() {
        return drawShadow;
    }

    public int getShadowSize() {
        return shadowSize;
    }

    public float getHoverAmount() {
        return hoverAmount;
    }

    public boolean isRippleActive() {
        return rippleActive;
    }

    public void setUseGradient(boolean useGradient) {
        this.useGradient = useGradient;
        repaint();
    }

    public boolean isUseGradient() {
        return useGradient;
    }

    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        repaint();
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public boolean isHovered() {
        return isHovered;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public void setDrawBorder(boolean drawBorder) {
        this.drawBorder = drawBorder;
        repaint();
    }

    public boolean isDrawBorder() {
        return drawBorder;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
        repaint();
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint();
    }

    public Color getBorderColor() {
        return borderColor;
    }
}
