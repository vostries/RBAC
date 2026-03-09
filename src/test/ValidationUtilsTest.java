import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {

    @Test
    void validUsername() {
        assertTrue(ValidationUtils.isValidUsername("john_doe"));
        assertFalse(ValidationUtils.isValidUsername("ab"));
        assertFalse(ValidationUtils.isValidUsername("user!"));
    }

    @Test
    void validEmail() {
        assertTrue(ValidationUtils.isValidEmail("user@example.com"));
        assertFalse(ValidationUtils.isValidEmail("badmail"));
        assertFalse(ValidationUtils.isValidEmail("user@no-domain"));
    }

    @Test
    void validDate() {
        assertTrue(ValidationUtils.isValidDate("2026-03-02"));
        assertTrue(ValidationUtils.isValidDate("2026-03-02 12:30"));
        assertFalse(ValidationUtils.isValidDate("02-03-2026"));
    }

    @Test
    void normalizeStringAndRequireNonEmpty() {
        assertEquals("hello world", ValidationUtils.normalizeString("  hello   world  "));
        assertEquals("", ValidationUtils.normalizeString(null));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireNonEmpty("  ", "field"));
    }
}

