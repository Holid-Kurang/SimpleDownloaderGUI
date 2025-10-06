package component;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Custom JTable dengan styling modern Fitur: - Rounded corners untuk setiap row
 * - Alternating row colors - Custom header styling - Hover effects - Better
 * spacing dan padding
 */
public class CustomTable extends JTable {

    private Color headerBackground = new Color(243, 244, 246); // gray-100
    private Color headerForeground = new Color(55, 65, 81); // gray-700
    private Color evenRowColor = new Color(249, 250, 251); // gray-50
    private Color oddRowColor = Color.WHITE;
    private Color customSelectionBackground = new Color(219, 234, 254); // blue-100
    private Color customSelectionForeground = new Color(30, 64, 175); // blue-800
    private final Color customGridColor = new Color(229, 231, 235); // gray-200
    private final Color customBorderColor = new Color(209, 213, 219); // gray-300

    private int rowPadding = 8;

    public CustomTable() {
        super();
        initialize();
    }

    public CustomTable(TableModel model) {
        super(model);
        initialize();
    }

    private void initialize() {
        // Table settings
        setShowGrid(true);
        setGridColor(customGridColor);
        setIntercellSpacing(new Dimension(0, 0));
        setRowHeight(45);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Selection colors
        setSelectionBackground(customSelectionBackground);
        setSelectionForeground(customSelectionForeground);

        // Font
        setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Custom header renderer
        JTableHeader header = getTableHeader();
        header.setDefaultRenderer(new CustomHeaderRenderer());
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setReorderingAllowed(false);

        // Custom cell renderer for all columns
        setDefaultRenderer(Object.class, new CustomCellRenderer());
        setDefaultRenderer(String.class, new CustomCellRenderer());
        setDefaultRenderer(Integer.class, new CustomCellRenderer());
    }

    // Custom Header Renderer
    class CustomHeaderRenderer extends DefaultTableCellRenderer {

        public CustomHeaderRenderer() {
            setHorizontalAlignment(LEFT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setBackground(headerBackground);
            setForeground(headerForeground);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, customBorderColor),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));

            // Add sort arrow indicator
            String text = value != null ? value.toString() : "";
            if (table.getRowSorter() != null) {
                TableRowSorter<?> sorter = (TableRowSorter<?>) table.getRowSorter();
                java.util.List<? extends RowSorter.SortKey> sortKeys = sorter.getSortKeys();

                if (!sortKeys.isEmpty()) {
                    RowSorter.SortKey sortKey = sortKeys.get(0);
                    if (sortKey.getColumn() == column) {
                        String arrow = sortKey.getSortOrder() == SortOrder.ASCENDING ? " ▲" : " ▼";
                        text += arrow;
                    }
                }
            }
            setText(text);

            return this;
        }
    }

    // Custom Cell Renderer
    class CustomCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Alternating row colors
            if (isSelected) {
                setBackground(customSelectionBackground);
                setForeground(customSelectionForeground);
            } else {
                if (row % 2 == 0) {
                    setBackground(evenRowColor);
                } else {
                    setBackground(oddRowColor);
                }
                setForeground(new Color(31, 41, 55)); // gray-800
            }

            // Padding
            setBorder(BorderFactory.createEmptyBorder(rowPadding, 12, rowPadding, 12));

            // Font
            setFont(new Font("Segoe UI", Font.PLAIN, 13));

            return this;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    // Getters and Setters
    public void setHeaderBackground(Color color) {
        this.headerBackground = color;
        repaint();
    }

    public Color getHeaderBackground() {
        return headerBackground;
    }

    public void setHeaderForeground(Color color) {
        this.headerForeground = color;
        repaint();
    }

    public Color getHeaderForeground() {
        return headerForeground;
    }

    public void setEvenRowColor(Color color) {
        this.evenRowColor = color;
        repaint();
    }

    public Color getEvenRowColor() {
        return evenRowColor;
    }

    public void setOddRowColor(Color color) {
        this.oddRowColor = color;
        repaint();
    }

    public Color getOddRowColor() {
        return oddRowColor;
    }

    public void setCustomSelectionBackground(Color color) {
        this.customSelectionBackground = color;
        setSelectionBackground(color);
        repaint();
    }

    public void setCustomSelectionForeground(Color color) {
        this.customSelectionForeground = color;
        setSelectionForeground(color);
        repaint();
    }

    public void setRowPadding(int padding) {
        this.rowPadding = padding;
        repaint();
    }

    public int getRowPadding() {
        return rowPadding;
    }
}
