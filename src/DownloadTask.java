
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class DownloadTask {

    enum Status {
        QUEUED, DOWNLOADING, PAUSED, COMPLETED, ERROR, CANCELED
    }

    final URL url;
    final File outputFile;
    volatile long contentLength = -1L;
    volatile long downloaded = 0L;
    volatile Status status = Status.QUEUED;
    final long addedAt = System.currentTimeMillis();
    private final AtomicBoolean cancel = new AtomicBoolean(false);
    private volatile boolean pauseRequested = false;
    private Thread worker;
    private long speedWindowBytes = 0L;
    private long speedWindowStart = System.currentTimeMillis();
    private double lastSpeed = 0.0; // bytes/sec

    public DownloadTask(URL url, File outputFile) {
        this.url = url;
        this.outputFile = outputFile;
    }

    void start(DownloadTableModel model) {
        if (worker != null && worker.isAlive()) {
            return;
        }
        cancel.set(false);
        pauseRequested = false;
        worker = new Thread(() -> runDownload(model), "DL-" + outputFile.getName());
        worker.start();
    }

    void pause() {
        if (status == Status.DOWNLOADING) {
            pauseRequested = true;
        }
    }

    void resume(DownloadTableModel model) {
        if (status == Status.PAUSED || status == Status.ERROR) {
            start(model);
        }
    }

    void cancel() {
        cancel.set(true);
        pauseRequested = true;
    }

    int getProgressPercent() {
        if (contentLength <= 0) {
            return -1; // unknown => indeterminate

        }
        return (int) Math.min(100, Math.round((downloaded * 100.0) / contentLength));
    }

    double getSpeedBytesPerSec() {
        return lastSpeed;
    }

    private void runDownload(DownloadTableModel model) {
        status = Status.DOWNLOADING;
        model.updated(this);
        try {
            long existing = outputFile.exists() ? outputFile.length() : 0L;
            downloaded = existing;

            URI base = URI.create(url.toString());
            URLConnection conn = openWithRedirect(url, base, existing);
            contentLength = conn.getContentLengthLong();
            if (contentLength > 0 && existing > 0 && existing > contentLength) {
                // local file larger than remote; restart
                existing = 0L;
                downloaded = 0L;
            }

            try (InputStream in = new BufferedInputStream(conn.getInputStream()); OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile, existing > 0))) {

                byte[] buf = new byte[16 * 1024];
                int read;
                speedWindowBytes = 0;
                speedWindowStart = System.currentTimeMillis();
                while ((read = in.read(buf)) != -1) {
                    if (cancel.get()) {
                        status = Status.CANCELED;
                        model.updated(this);
                        return;
                    }
                    if (pauseRequested) {
                        status = Status.PAUSED;
                        model.updated(this);
                        return;
                    }
                    out.write(buf, 0, read);
                    downloaded += read;
                    speedWindowBytes += read;
                    updateSpeedIfNeeded(model);
                    if (downloaded % (128 * 1024) == 0) { // reduce UI churn
                        model.updated(this);
                    }
                }
                out.flush();
            }
            status = Status.COMPLETED;
            model.updated(this);
        } catch (IOException ex) {
            status = Status.ERROR;
            model.updated(this);
        }
    }

    private void updateSpeedIfNeeded(DownloadTableModel model) {
        long now = System.currentTimeMillis();
        long dt = now - speedWindowStart;
        if (dt >= 800) {
            lastSpeed = speedWindowBytes * 1000.0 / Math.max(1, dt);
            speedWindowBytes = 0;
            speedWindowStart = now;
            model.updated(this);
        }
    }

    private static URLConnection openWithRedirect(URL url, URI baseUri, long existingBytes) throws IOException {
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(10_000);
        conn.setRequestProperty("User-Agent", "DownloaderMini/1.0");
        if (existingBytes > 0) {
            // Attempt resume
            conn.setRequestProperty("Range", "bytes=" + existingBytes + "-");
        }

        if (conn instanceof HttpURLConnection http) {
            http.setInstanceFollowRedirects(false); // handle manually to preserve Range
            int status = http.getResponseCode();
            if (status >= 300 && status < 400) {
                String loc = http.getHeaderField("Location");
                http.disconnect();
                if (loc != null && !loc.isEmpty()) {
                    URI redirected = baseUri.resolve(loc);
                    return openWithRedirect(redirected.toURL(), redirected, existingBytes);
                }
            } else if (status == 200 && existingBytes > 0) {
                // Server ignored Range; restart from 0
                http.disconnect();
                return openWithRedirect(url, baseUri, 0);
            } else if (status >= 400) {
                // Throw to mark ERROR
                InputStream err = http.getErrorStream();
                if (err != null) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(err, StandardCharsets.UTF_8))) {
                        String line;
                        StringBuilder sb = new StringBuilder();
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append('\n');
                        }
                        throw new IOException("HTTP " + status + "\n" + sb);
                    }
                }
                throw new IOException("HTTP " + status);
            }
        }
        return conn;
    }
}
