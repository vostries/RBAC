import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    void log(String action, String performer, String target, String details) {
        String ts = LocalDateTime.now().toString();
        String normalizedAction = ValidationUtils.normalizeString(action);
        String normalizedPerformer = ValidationUtils.normalizeString(performer);
        String normalizedTarget = ValidationUtils.normalizeString(target);
        String normalizedDetails = details == null ? "" : details;
        entries.add(new AuditEntry(ts, normalizedAction, normalizedPerformer, normalizedTarget, normalizedDetails));
    }

    List<AuditEntry> getAll() {
        return List.copyOf(entries);
    }

    List<AuditEntry> getByPerformer(String performer) {
        if (performer == null) {
            return List.of();
        }
        String p = performer.trim();
        return entries.stream()
                .filter(e -> e.performer() != null && e.performer().equals(p))
                .toList();
    }

    List<AuditEntry> getByAction(String action) {
        if (action == null) {
            return List.of();
        }
        String a = action.trim();
        return entries.stream()
                .filter(e -> e.action() != null && e.action().equalsIgnoreCase(a))
                .toList();
    }

    void printLog() {
        if (entries.isEmpty()) {
            System.out.println("Аудит‑лог пуст.");
            return;
        }
        System.out.println("\n=== Аудит‑лог ===");
        for (AuditEntry e : entries) {
            System.out.printf("[%s] %s | %s -> %s%n", e.timestamp(), e.action(), e.performer(), e.target());
            if (e.details() != null && !e.details().isBlank()) {
                System.out.println("  " + e.details());
            }
        }
        System.out.println();
    }

    void saveToFile(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Имя файла для аудита не указано");
        }
        Path path = Path.of(filename.trim());
        StringBuilder sb = new StringBuilder();
        for (AuditEntry e : entries) {
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
}

