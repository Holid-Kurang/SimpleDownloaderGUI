
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class DownloadTableModel extends AbstractTableModel {

    private final String[] cols = {"Name", "Size", "Progress", "Speed", "Status", "Added"};
    private final List<DownloadTask> items = new ArrayList<>();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneId.systemDefault());

    public void add(DownloadTask t) {
        items.add(t);
        int idx = items.size() - 1;
        fireTableRowsInserted(idx, idx);
    }

    public void remove(DownloadTask t) {
        int idx = items.indexOf(t);
        if (idx >= 0) {
            items.remove(idx);
            fireTableRowsDeleted(idx, idx);
        }
    }

    public DownloadTask get(int row) {
        return items.get(row);
    }

    public int indexOf(DownloadTask t) {
        return items.indexOf(t);
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return cols.length;
    }

    @Override
    public String getColumnName(int column) {
        return cols[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 2) {
            return Integer.class; // progress percent
        }
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DownloadTask t = items.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> t.outputFile.getName();
            case 1 -> humanSize(t.contentLength);
            case 2 -> t.getProgressPercent();
            case 3 -> humanSpeed(t.getSpeedBytesPerSec());
            case 4 -> t.status.toString();
            case 5 -> fmt.format(Instant.ofEpochMilli(t.addedAt));
            default -> "";
        };
    }

    void updated(DownloadTask t) {
        int idx = indexOf(t);
        if (idx >= 0) {
            fireTableRowsUpdated(idx, idx);
        }
    }

    private static String humanSize(long bytes) {
        if (bytes <= 0) {
            return "?";
        }
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        double b = bytes;
        int i = 0;
        while (b >= 1024 && i < units.length - 1) {
            b /= 1024;
            i++;
        }
        return new DecimalFormat("0.##").format(b) + " " + units[i];
    }

    private static String humanSpeed(double bps) {
        if (bps <= 0) {
            return "-";
        }
        String[] units = {"B/s", "KB/s", "MB/s", "GB/s"};
        double b = bps;
        int i = 0;
        while (b >= 1024 && i < units.length - 1) {
            b /= 1024;
            i++;
        }
        return new DecimalFormat("0.##").format(b) + " " + units[i];
    }
}
