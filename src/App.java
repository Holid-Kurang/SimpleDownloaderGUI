
import javax.swing.*;
import javax.swing.table.TableRowSorter;

import component.CustomButton;
import component.CustomTable;
import component.CustomTextField;
import component.ProgressRenderer;
import component.StatusRenderer;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class App extends JFrame {

    private final CustomTextField urlField = new CustomTextField("Masukkan URL (http://...)", 40);
    private final CustomTextField nameField = new CustomTextField("Contoh: Dokumen-Penting.pdf", 20);
    private final CustomButton addButton = new CustomButton("Add");
    private final CustomButton pauseButton = new CustomButton("Pause");
    private final CustomButton resumeButton = new CustomButton("Resume");
    private final CustomButton removeButton = new CustomButton("Remove");
    private final CustomButton openFileButton = new CustomButton("Open File");
    private final CustomButton openFolderButton = new CustomButton("Open Folder");
    private final CustomButton browseButton = new CustomButton("Browse...");

    private final CustomTextField searchField = new CustomTextField("", 20);

    private final DownloadTableModel tableModel = new DownloadTableModel();
    private final CustomTable table = new CustomTable(tableModel);
    private final TableRowSorter<DownloadTableModel> sorter;

    public App() {
        super("Downloader Simple");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        // Set background color
        getContentPane().setBackground(Color.WHITE);

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(Color.WHITE);
        top.setBorder(BorderFactory.createEmptyBorder(20, 15, 15, 15));

        // Title Section Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        JLabel titleLabel = new JLabel("Manajer Unduhan Lengkap");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(31, 41, 55)); // gray-800
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));

        JLabel subtitleLabel = new JLabel("Kontrol penuh atas unduhan Anda.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 114, 128)); // gray-500
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(subtitleLabel);

        top.add(titlePanel);

        // URL Section
        JLabel urlLabel = new JLabel("URL:");
        urlLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        urlLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(urlLabel);
        top.add(Box.createVerticalStrut(5));

        urlField.setText("http://info.cern.ch/index.html");
        urlField.setAlignmentX(Component.LEFT_ALIGNMENT);
        urlField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        top.add(urlField);
        top.add(Box.createVerticalStrut(15));

        // Save as Section
        JLabel nameLabel = new JLabel("Simpan Sebagai (optional):");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(nameLabel);
        top.add(Box.createVerticalStrut(5));

        // Panel untuk nameField dan browseButton
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
        namePanel.setBackground(Color.WHITE);
        namePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        namePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        namePanel.add(nameField);
        namePanel.add(Box.createHorizontalStrut(8));

        browseButton.setMaximumSize(new Dimension(100, 50));
        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("downloaded.file"));
            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                nameField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        namePanel.add(browseButton);

        top.add(namePanel);
        top.add(Box.createVerticalStrut(15));

        pauseButton.setBackgroundColor(Color.decode("#F4D03F"));
        resumeButton.setBackgroundColor(Color.decode("#00C853"));
        removeButton.setBackgroundColor(Color.decode("#fb2c36"));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.setBackground(Color.WHITE);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        buttons.add(addButton);
        buttons.add(pauseButton);
        buttons.add(resumeButton);
        buttons.add(removeButton);
        buttons.add(openFileButton);
        buttons.add(openFolderButton);
        top.add(buttons);
        top.add(Box.createVerticalStrut(15));

        JPanel findPanel = new JPanel();
        findPanel.setLayout(new BoxLayout(findPanel, BoxLayout.Y_AXIS));
        findPanel.setBackground(Color.WHITE);
        findPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        findPanel.add(searchLabel);
        findPanel.add(Box.createVerticalStrut(5));

        searchField.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        findPanel.add(searchField);

        top.add(findPanel);

        add(top, BorderLayout.NORTH);

        table.setRowHeight(24);
        table.setBackground(Color.WHITE);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        table.getColumnModel().getColumn(2).setCellRenderer(new ProgressRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        add(scroll, BorderLayout.CENTER);

        WireActions();
        WireSearch();
        WireSelectionEnablement();

        setSize(1000, 700);
        setLocationRelativeTo(null);
    }

    private void WireActions() {
        addButton.addActionListener((ActionEvent e) -> {
            String urlText = urlField.getText().trim();
            String nameText = nameField.getText().trim();
            if (urlText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "URL tidak boleh kosong", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                URI uri = URI.create(urlText);
                URL url = uri.toURL();
                File out = nameText.isEmpty() ? new File(deriveFilename(url)) : new File(nameText);
                DownloadTask task = new DownloadTask(url, out);
                tableModel.add(task);
                task.start(tableModel);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "URL tidak valid: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (MalformedURLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan unduhan: " + ex, "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        pauseButton.addActionListener(e -> actOnSelected(DownloadTask::pause));
        resumeButton.addActionListener(e -> actOnSelected(t -> t.resume(tableModel)));
        removeButton.addActionListener(e -> actOnSelected(t -> {
            t.cancel();
            tableModel.remove(t);
        }));

        openFileButton.addActionListener(e -> actOnSelected(t -> {
            try {
                if (!t.outputFile.exists()) {
                    JOptionPane.showMessageDialog(this, "File belum ada atau belum selesai diunduh.", "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(t.outputFile);
                } else {
                    JOptionPane.showMessageDialog(this, "Desktop API tidak didukung di sistem ini.", "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (HeadlessException | IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal membuka file: " + ex, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }));

        openFolderButton.addActionListener(e -> actOnSelected(t -> {
            try {
                openFolderFor(t.outputFile);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal membuka folder: " + ex, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }));
    }

    private void WireSelectionEnablement() {
        ListSelectionModel sel = table.getSelectionModel();
        sel.addListSelectionListener(e -> updateButtonsEnabled());
        updateButtonsEnabled();
    }

    private void updateButtonsEnabled() {
        boolean hasSel = table.getSelectedRow() >= 0;
        pauseButton.setEnabled(hasSel);
        resumeButton.setEnabled(hasSel);
        removeButton.setEnabled(hasSel);
        if (!hasSel) {
            openFileButton.setEnabled(false);
            openFolderButton.setEnabled(false);
            return;
        }
        int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
        DownloadTask t = tableModel.get(modelRow);
        openFolderButton.setEnabled(true);
        openFileButton.setEnabled(t.outputFile.exists());
    }

    private void openFolderFor(File f) throws IOException {
        File target = f.exists() ? f : f.getParentFile();
        if (target == null) {
            target = new File(".");
        }
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win") && f.exists()) {
            String path = f.getAbsolutePath().replace('/', '\\');
            try {
                new ProcessBuilder("explorer.exe", "/select,", path).start();
                return;
            } catch (IOException ignore) {
            }
        }
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(target.isDirectory() ? target : target.getParentFile());
        } else {
            throw new IOException("Desktop API tidak didukung");
        }
    }

    private void WireSearch() {
        DocumentListener dl = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }
        };
        searchField.getDocument().addDocumentListener(dl);
        applyFilter();
    }

    private void applyFilter() {
        String q = searchField.getText();
        if (q == null) {
            q = "";
        }
        final String query = q.trim().toLowerCase();
        if (query.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        sorter.setRowFilter(new RowFilter<DownloadTableModel, Integer>() {
            @Override
            public boolean include(RowFilter.Entry<? extends DownloadTableModel, ? extends Integer> entry) {
                String name = String.valueOf(entry.getValue(0)).toLowerCase(); // Name column
                String status = String.valueOf(entry.getValue(4)).toLowerCase(); // Status column
                return name.contains(query) || status.contains(query);
            }
        });
    }

    private void actOnSelected(TaskAction action) {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        DownloadTask task = tableModel.get(modelRow);
        action.run(task);
    }

    private static String deriveFilename(URL url) {
        String path = url.getPath();
        if (path == null || path.isEmpty() || path.endsWith("/")) {
            return "download.bin";
        }
        String name = path.substring(path.lastIndexOf('/') + 1);
        return name.isEmpty() ? "download.bin" : name;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}
