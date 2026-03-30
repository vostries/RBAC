import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

final class AuditLog {

    public record AuditEntry(
            String timestamp,
            String action,
            String performer,
            String target,
            String details
    ) {
    }

    private final List<AuditEntry> entries = new ArrayList<>();
    private final BlockingQueue<AuditEntry> queue = new LinkedBlockingQueue<>();
    private final Thread worker;
    private volatile boolean running = true;

    AuditLog() {
        worker = new Thread(this::consumeLoop, "audit-log-worker");
        worker.setDaemon(true);
        worker.start();
    }

    void log(String action, String performer, String target, String details) {
        String ts = LocalDateTime.now().toString();
        String normalizedAction = ValidationUtils.normalizeString(action);
        String normalizedPerformer = ValidationUtils.normalizeString(performer);
        String normalizedTarget = ValidationUtils.normalizeString(target);
        String normalizedDetails = details == null ? "" : details;
        queue.offer(new AuditEntry(ts, normalizedAction, normalizedPerformer, normalizedTarget, normalizedDetails));
    }

    List<AuditEntry> getAll() {
        flush();
        synchronized (entries) {
            return List.copyOf(entries);
        }
    }

    List<AuditEntry> getByPerformer(String performer) {
        flush();
        if (performer == null) {
            return List.of();
        }
        String p = performer.trim();
        return getAll().stream()
                .filter(e -> e.performer() != null && e.performer().equals(p))
                .toList();
    }

    List<AuditEntry> getByAction(String action) {
        flush();
        if (action == null) {
            return List.of();
        }
        String a = action.trim();
        return getAll().stream()
                .filter(e -> e.action() != null && e.action().equalsIgnoreCase(a))
                .toList();
    }

    void printLog() {
        flush();
        if (entries.isEmpty()) {
            System.out.println("Аудит‑лог пуст.");
            return;
        }
        System.out.println("\n=== Аудит‑лог ===");
        for (AuditEntry e : getAll()) {
            System.out.printf("[%s] %s | %s -> %s%n", e.timestamp(), e.action(), e.performer(), e.target());
            if (e.details() != null && !e.details().isBlank()) {
                System.out.println("  " + e.details());
            }
        }
        System.out.println();
    }

    void saveToFile(String filename) {
        flush();
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Имя файла для аудита не указано");
        }
        Path path = Path.of(filename.trim());
        StringBuilder sb = new StringBuilder();
        for (AuditEntry e : getAll()) {
            sb.append(e.timestamp()).append(" | ")
                    .append(e.action()).append(" | ")
                    .append(e.performer()).append(" | ")
                    .append(e.target()).append(" | ")
                    .append(e.details() == null ? "" : e.details())
                    .append(System.lineSeparator());
        }
        try {
            Files.writeString(path, sb.toString());
        } catch (IOException ex) {
            throw new RuntimeException("Не удалось сохранить аудит‑лог: " + ex.getMessage(), ex);
        }
    }

    void shutdown() {
        running = false;
        worker.interrupt();
        flush();
    }

    private void consumeLoop() {
        while (running || !queue.isEmpty()) {
            try {
                AuditEntry entry = queue.poll(200, TimeUnit.MILLISECONDS);
                if (entry != null) {
                    synchronized (entries) {
                        entries.add(entry);
                    }
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        AuditEntry remaining;
        while ((remaining = queue.poll()) != null) {
            synchronized (entries) {
                entries.add(remaining);
            }
        }
    }

    private void flush() {
        long deadline = System.currentTimeMillis() + 1000;
        while (!queue.isEmpty() && System.currentTimeMillis() < deadline) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}

