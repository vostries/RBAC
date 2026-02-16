import java.time.LocalDateTime;

public record AssignmentMetadata(String assignedBy, String assignedAt, String reason) {

    public static AssignmentMetadata now(String assignedBy, String reason) {
        return new AssignmentMetadata(assignedBy, LocalDateTime.now().toString(), reason);
    }

    public String format() {
        String r = reason != null && !reason.isBlank() ? reason : "—";
        return String.format("Назначил: %s, дата: %s, причина: %s", assignedBy, assignedAt, r);
    }
}
