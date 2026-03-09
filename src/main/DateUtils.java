import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

final class DateUtils {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateUtils() {
    }

    static String getCurrentDate() {
        return LocalDate.now().format(DATE_FMT);
    }

    static String getCurrentDateTime() {
        return LocalDateTime.now().format(DATE_TIME_FMT);
    }

    static boolean isBefore(String date1, String date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        String d1 = date1.trim();
        String d2 = date2.trim();
        return d1.compareTo(d2) < 0;
    }

    static boolean isAfter(String date1, String date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        String d1 = date1.trim();
        String d2 = date2.trim();
        return d1.compareTo(d2) > 0;
    }

    static String addDays(String date, int days) {
        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("Дата не указана");
        }
        LocalDate base = LocalDate.parse(date.trim(), DATE_FMT);
        return base.plusDays(days).format(DATE_FMT);
    }

    static String formatRelativeTime(String date) {
        if (date == null || date.isBlank()) {
            return "";
        }
        LocalDate target = LocalDate.parse(date.trim(), DATE_FMT);
        LocalDate today = LocalDate.now();
        long diff = target.toEpochDay() - today.toEpochDay();
        if (diff == 0) {
            return "today";
        } else if (diff > 0) {
            if (diff == 1) return "in 1 day";
            return "in " + diff + " days";
        } else {
            long daysAgo = -diff;
            if (daysAgo == 1) return "1 day ago";
            return daysAgo + " days ago";
        }
    }
}

