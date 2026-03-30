import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TemporaryAssignment extends AbstractRoleAssignment {
    private String expiresAt;
    private boolean autoRenew = false;
    private volatile boolean deactivatedByScheduler = false;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TemporaryAssignment(User user, Role role, AssignmentMetadata metadata, String expiresAt) {
        super(user, role, metadata);
        this.expiresAt = expiresAt != null ? expiresAt : "";
    }

    public void extend(String newExpirationDate) {
        if (newExpirationDate == null || newExpirationDate.isBlank())
            throw new IllegalArgumentException("Укажите дату окончания");
        this.expiresAt = newExpirationDate;
        this.deactivatedByScheduler = false;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        return isExpiredAt(LocalDateTime.now());
    }

    public boolean isExpiredAt(LocalDateTime current) {
        if (expiresAt == null || expiresAt.isBlank())
            return true;
        try {
            LocalDateTime end = expiresAt.length() > 10
                    ? LocalDateTime.parse(expiresAt, FMT)
                    : LocalDateTime.parse(expiresAt + " 23:59", FMT);
            return end.isBefore(current);
        } catch (DateTimeParseException e) {
            return true;
        }
    }

    public String getTimeRemaining() {
        if (expiresAt == null || expiresAt.isBlank())
            return "Срок не задан";
        try {
            LocalDateTime end = expiresAt.length() > 10
                    ? LocalDateTime.parse(expiresAt, FMT)
                    : LocalDateTime.parse(expiresAt + " 23:59", FMT);
            if (end.isBefore(LocalDateTime.now()))
                return "Истекло";
            return "До " + expiresAt;
        } catch (DateTimeParseException e) {
            return "Некорректный формат даты";
        }
    }

    @Override
    public boolean isActive() {
        return !deactivatedByScheduler && !isExpired();
    }

    public synchronized boolean deactivateIfExpired() {
        if (!deactivatedByScheduler && isExpired()) {
            deactivatedByScheduler = true;
            return true;
        }
        return false;
    }

    @Override
    public String assignmentType() {
        return "TEMPORARY";
    }

    @Override
    public String summary() {
        return super.summary() + "\nДействует до: " + (expiresAt != null ? expiresAt : "—");
    }
}
