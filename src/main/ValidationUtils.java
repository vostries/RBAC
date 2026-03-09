import java.util.regex.Pattern;

final class ValidationUtils {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    // yyyy-MM-dd or yyyy-MM-dd HH:MM
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}( \\d{2}:\\d{2})?$");

    private ValidationUtils() {
    }

    static boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }

    static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    static boolean isValidDate(String date) {
        if (date == null) {
            return false;
        }
        return DATE_PATTERN.matcher(date.trim()).matches();
    }

    static String normalizeString(String input) {
        if (input == null) {
            return "";
        }
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        // заменяем последовательности пробелов на один пробел
        return trimmed.replaceAll("\\s+", " ");
    }

    static void requireNonEmpty(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            String name = fieldName != null && !fieldName.isBlank() ? fieldName : "Поле";
            throw new IllegalArgumentException(name + " не может быть пустым");
        }
    }
}

