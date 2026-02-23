import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

final class AssignmentFilters {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private AssignmentFilters() {}

    private static LocalDateTime parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            // ISO format from LocalDateTime.toString()
            if (s.contains("T")) return LocalDateTime.parse(s);
            return s.length() > 10 ? LocalDateTime.parse(s, FMT) : LocalDateTime.parse(s + " 23:59", FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    static AssignmentFilter byUser(User user) {
        return a -> a != null && user != null && a.user().equals(user);
    }

    static AssignmentFilter byUsername(String username) {
        return a -> a != null && username != null && a.user().username().equals(username);
    }

    static AssignmentFilter byRole(Role role) {
        return a -> a != null && role != null && a.role().equals(role);
    }

    static AssignmentFilter byRoleName(String roleName) {
        return a -> a != null && roleName != null && a.role().getName().equals(roleName);
    }

    static AssignmentFilter activeOnly() {
        return a -> a != null && a.isActive();
    }

    static AssignmentFilter inactiveOnly() {
        return a -> a != null && !a.isActive();
    }

    static AssignmentFilter byType(String type) {
        return a -> a != null && type != null && a.assignmentType().equalsIgnoreCase(type);
    }

    static AssignmentFilter assignedBy(String username) {
        return a -> a != null && username != null
                && a.metadata().assignedBy() != null
                && a.metadata().assignedBy().equals(username);
    }

    static AssignmentFilter assignedAfter(String date) {
        if (date == null) return a -> false;
        LocalDateTime after = parseDate(date);
        if (after == null) return a -> false;
        return a -> {
            if (a == null) return false;
            LocalDateTime at = parseDate(a.metadata().assignedAt());
            return at != null && at.isAfter(after);
        };
    }

    static AssignmentFilter expiringBefore(String date) {
        if (date == null) return a -> false;
        LocalDateTime before = parseDate(date);
        if (before == null) return a -> false;
        return a -> {
            if (!(a instanceof TemporaryAssignment ta)) return false;
            String exp = ta.getExpiresAt();
            if (exp == null || exp.isBlank()) return false;
            LocalDateTime end = parseDate(exp);
            return end != null && end.isBefore(before);
        };
    }

}
