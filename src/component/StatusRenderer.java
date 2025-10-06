package component;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

public class StatusRenderer extends JLabel implements TableCellRenderer {

    public StatusRenderer() {
        setOpaque(true);
        setFont(new Font("Segoe UI", Font.BOLD, 11));
        setHorizontalAlignment(CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        String status = value != null ? value.toString() : "";
        setText(status);

        // Set colors based on status
        switch (status.toUpperCase()) {
            case "COMPLETED", "SELESAI" -> {
                setBackground(new Color(220, 252, 231)); // green-100
                setForeground(new Color(22, 163, 74)); // green-600
            }
            case "DOWNLOADING", "MENGUNDUH" -> {
                setBackground(new Color(219, 234, 254)); // blue-100
                setForeground(new Color(37, 99, 235)); // blue-600
            }
            case "PAUSED", "DIJEDA" -> {
                setBackground(new Color(254, 249, 195)); // yellow-100
                setForeground(new Color(202, 138, 4)); // yellow-600
            }
            case "ERROR", "GAGAL" -> {
                setBackground(new Color(254, 226, 226)); // red-100
                setForeground(new Color(220, 38, 38)); // red-600
            }
            case "QUEUED", "ANTRIAN" -> {
                setBackground(new Color(243, 244, 246)); // gray-100
                setForeground(new Color(75, 85, 99)); // gray-600
            }
            case "CANCELED", "DIBATALKAN" -> {
                setBackground(new Color(243, 244, 246)); // gray-100
                setForeground(new Color(107, 114, 128)); // gray-500
            }
            default -> {
                setBackground(new Color(243, 244, 246)); // gray-100
                setForeground(new Color(75, 85, 99)); // gray-600
            }
        }

        // Add padding
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 8, 4, 8),
                BorderFactory.createLineBorder(getForeground().brighter().brighter(), 1, true)
        ));

        return this;
    }
}
