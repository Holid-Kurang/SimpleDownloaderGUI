
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

public class DownloaderMini extends JFrame {

    private final CustomTextField urlField = new CustomTextField("Masukkan URL (http://...)", 40);
    private final CustomTextField nameField = new CustomTextField("Contoh: Dokumen-Penting.pdf", 20);
    private final CustomButton addButton = new CustomButton("Add");
    private final CustomButton pauseButton = new CustomButton("Pause");
    private final CustomButton resumeButton = new CustomButton("Resume");
    private final CustomButton removeButton = new CustomButton("Delete");
    private final CustomButton openFileButton = new CustomButton("Open File");
    private final CustomButton openFolderButton = new CustomButton("Open Folder");
    private final CustomButton browseButton = new CustomButton("Browse...");

    private final CustomTextField searchField = new CustomTextField("", 20);

    private final DownloadTableModel tableModel = new DownloadTableModel();
    private final CustomTable table = new CustomTable(tableModel);
    private final TableRowSorter<DownloadTableModel> sorter;

    public DownloaderMini() {
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
            // Usahakan nama awal mengikuti ekstensi dari URL saat ini
            String urlText = urlField.getText().trim();
            String nameText = nameField.getText().trim();
            File suggested = null;
            try {
                URL u = URI.create(urlText).toURL();
                String urlExt = getUrlExtension(u);
                if (nameText.isEmpty()) {
                    String base = stripExtension(deriveFilename(u));
                    suggested = new File(base + urlExt);
                } else {
                    File f = new File(nameText);
                    String base = stripExtension(f.getName());
                    File dir = f.getParentFile();
                    suggested = new File(dir == null ? new File(".") : dir, base + urlExt);
                }
            } catch (Exception ignore) {
                // fallback
                suggested = new File("downloaded.file");
            }
            chooser.setSelectedFile(suggested);
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
                File out = buildOutputFile(url, nameText);
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
            RemoveChoice decision = showRemoveChoiceDialog(t.outputFile);
            if (decision == RemoveChoice.CANCEL) {
                return;
            }

            // Batalkan dulu jika sedang berjalan
            t.cancel();
            File f = t.outputFile;

            if (decision == RemoveChoice.REMOVE_ONLY) {
                tableModel.remove(t);
                return;
            }

            // DELETE_FILE
            // Pastikan worker berhenti agar stream file tertutup
            if (t.isRunning()) {
                t.awaitStop(1500); // tunggu sampai 1.5 detik
            }
            tableModel.remove(t);
            if (f != null && f.exists()) {
                try {
                    boolean deleted = f.delete();
                    if (!deleted) {
                        // Coba retry singkat: tidur 200ms lalu coba lagi
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ignored) {
                        }
                        deleted = f.delete();
                    }
                    if (!deleted) {
                        // Jadwalkan penghapusan saat JVM keluar sebagai upaya terakhir
                        try {
                            f.deleteOnExit();
                        } catch (Throwable ignored) {
                        }
                        JOptionPane.showMessageDialog(DownloaderMini.this,
                                "Gagal menghapus file di disk sekarang. Akan dicoba saat aplikasi ditutup.",
                                "Info", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // Jika file berhasil dihapus, coba hapus folder induk jika kosong
                        tryDeleteParentIfEmpty(f);
                    }
                } catch (SecurityException se) {
                    JOptionPane.showMessageDialog(DownloaderMini.this,
                            "Tidak memiliki izin untuk menghapus file: " + se.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
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

    private enum RemoveChoice {
        REMOVE_ONLY, DELETE_FILE, CANCEL
    }

    // Dialog konfirmasi khusus dengan tombol bergaya sesuai main window:
    // - Pesan: "Hapus dari daftar saja tanpa menghapus file di disk?"
    // - Yes (hijau seperti Resume) -> REMOVE_ONLY
    // - No (merah seperti Delete) -> DELETE_FILE
    // Menutup dialog (X/ESC) -> CANCEL
    private RemoveChoice showRemoveChoiceDialog(File f) {
        final JDialog dialog = new JDialog(this, "Konfirmasi", true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel content = new JPanel();
        content.setLayout(new BorderLayout(12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel msg = new JLabel("<html>Hapus file dari disk juga?<br/>");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Panel tombol
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

        CustomButton yesBtn = new CustomButton("Yes");
        yesBtn.setBackgroundColor(Color.decode("#00C853")); // sama dengan tombol Resume
        yesBtn.setForeground(Color.WHITE);

        CustomButton noBtn = new CustomButton("No");
        noBtn.setBackgroundColor(Color.decode("#fb2c36")); // sama dengan tombol Delete
        noBtn.setForeground(Color.WHITE);

        final RemoveChoice[] result = new RemoveChoice[] { RemoveChoice.CANCEL };

        yesBtn.addActionListener(ev -> {
            result[0] = RemoveChoice.DELETE_FILE; // YES -> delete file
            dialog.dispose();
        });
        noBtn.addActionListener(ev -> {
            result[0] = RemoveChoice.REMOVE_ONLY; // NO -> remove from list only
            dialog.dispose();
        });

        buttons.add(yesBtn);
        buttons.add(noBtn);

        content.add(msg, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        return result[0];
    }

    // Bangun file keluaran: jika user mengisi nama, pakai nama tersebut tapi paksa
    // ekstensi mengikuti dari URL.
    // Aturan:
    // - Jika nameText kosong -> gunakan nama dari URL (fallback ke deriveFilename).
    // - Jika nameText berisi path/nama -> ambil direktori dari input (jika ada),
    // ambil base name tanpa ekstensi,
    // lalu tambahkan ekstensi dari URL. Jika URL tanpa ekstensi, pertahankan
    // ekstensi dari input (jika ada).
    private static File buildOutputFile(URL url, String nameText) {
        if (nameText == null || nameText.trim().isEmpty()) {
            return new File(deriveFilename(url));
        }
        File input = new File(nameText.trim());
        File dir = input.getParentFile();
        String inputName = input.getName();
        String base = stripExtension(inputName);
        String urlExt = getUrlExtension(url);
        String inputExt = getExtension(inputName);
        String finalExt = !urlExt.isEmpty() ? urlExt : inputExt; // prioritaskan ekstensi dari URL
        String finalName = base + finalExt;
        return new File(dir == null ? new File(".") : dir, finalName);
    }

    private static String getUrlExtension(URL url) {
        String path = url.getPath();
        if (path == null || path.isEmpty() || path.endsWith("/")) {
            return "";
        }
        String file = path.substring(path.lastIndexOf('/') + 1);
        // Buang query-like suffix jika ada (meski jarang ada di path)
        int q = file.indexOf('?');
        if (q >= 0)
            file = file.substring(0, q);
        int h = file.indexOf('#');
        if (h >= 0)
            file = file.substring(0, h);
        String ext = getExtension(file);
        return ext;
    }

    private static String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot <= 0 || dot == filename.length() - 1) {
            return "";
        }
        return filename.substring(dot); // termasuk titik, mis. ".pdf"
    }

    private static String stripExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot <= 0) {
            return filename;
        }
        return filename.substring(0, dot);
    }

    // Mencoba menghapus folder induk jika kosong (aman: hanya folder langsung di
    // atas file)
    private static void tryDeleteParentIfEmpty(File file) {
        File parent = file.getParentFile();
        if (parent == null)
            return;
        if (!parent.isDirectory())
            return;
        String[] children = parent.list();
        if (children != null && children.length == 0) {
            try {
                parent.delete();
            } catch (SecurityException ignored) {
                // Abaikan jika tidak punya izin; tidak kritikal
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DownloaderMini().setVisible(true));
    }
}
