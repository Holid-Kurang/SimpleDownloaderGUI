package component;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

public class ProgressRenderer extends JComponent implements TableCellRenderer {

    private int value = 0;
    private int maximum = 100;
    private boolean isIndeterminate = false;
    private String progressText = "";
    private Color backgroundColor = new Color(243, 244, 246); // gray-100
    private Color progressColor = new Color(34, 197, 94); // green-500
    private Color textColor = new Color(31, 41, 55); // gray-800
    private int cornerRadius = 8;
    private final int horizontalPadding = 8;

    public ProgressRenderer() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(2, horizontalPadding, 2, horizontalPadding));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        int v = (value instanceof Integer) ? (Integer) value : 0;
        this.isIndeterminate = v < 0;
        this.value = Math.max(0, v);
        this.progressText = v < 0 ? "?" : v + "%";

        // Adjust colors based on selection
        if (isSelected) {
            this.backgroundColor = new Color(219, 234, 254); // blue-100
            this.textColor = new Color(30, 64, 175); // blue-800
        } else {
            this.backgroundColor = new Color(243, 244, 246); // gray-100
            this.textColor = new Color(31, 41, 55); // gray-800
        }

        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int progressHeight = getHeight();
        int y = (getHeight() - progressHeight) / 2;

        // Draw background
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(0, y, width, progressHeight, cornerRadius, cornerRadius);

        // Draw progress
        if (!isIndeterminate && value > 0) {
            int progressWidth = (int) ((double) value / maximum * width);
            g2d.setColor(progressColor);
            g2d.fillRoundRect(0, y, progressWidth, progressHeight, cornerRadius, cornerRadius);
        }

        // Draw text
        if (progressText != null && !progressText.isEmpty()) {
            g2d.setColor(textColor);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));

            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(progressText);
            int textHeight = fm.getAscent();
            int textX = (width - textWidth) / 2;
            int textY = y + (progressHeight + textHeight) / 2 - 2;

            g2d.drawString(progressText, textX, textY);
        }

        g2d.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(100, getWidth() + 4);
    }

    // Getters and Setters
    public void setValue(int value) {
        this.value = value;
        repaint();
    }

    public int getValue() {
        return value;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
        repaint();
    }

    public int getMaximum() {
        return maximum;
    }

    public void setIndeterminate(boolean indeterminate) {
        this.isIndeterminate = indeterminate;
        repaint();
    }

    public boolean isIndeterminate() {
        return isIndeterminate;
    }

    public void setProgressColor(Color color) {
        this.progressColor = color;
        repaint();
    }

    public Color getProgressColor() {
        return progressColor;
    }

    public void setProgressBackground(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    public Color getProgressBackground() {
        return backgroundColor;
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public int getProgressHeight() {
        return getHeight();
    }

    public int getHorizontalPadding() {
        return horizontalPadding;
    }
}
