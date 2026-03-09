import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FormatUtilsTest {

    @Test
    void formatTableProducesBorders() {
        String[] headers = {"Username", "Email"};
        List<String[]> rows = List.of(
                new String[]{"admin", "admin@mail.com"},
                new String[]{"user", "user@mail.com"}
        );
        String table = FormatUtils.formatTable(headers, rows);
        assertTrue(table.contains("Username"));
        assertTrue(table.contains("admin"));
        assertTrue(table.startsWith("+"));
    }

    @Test
    void formatBoxWrapsText() {
        String box = FormatUtils.formatBox("Hello");
        assertTrue(box.contains("Hello"));
        assertTrue(box.startsWith("+"));
    }

    @Test
    void truncateAndPad() {
        assertEquals("abcdef", FormatUtils.truncate("abcdef", 6));
        assertEquals("abc...", FormatUtils.truncate("abcdefgh", 6));
        assertEquals("abc", FormatUtils.truncate("abc", 6));
        assertEquals("abc   ", FormatUtils.padRight("abc", 6));
        assertEquals("   abc", FormatUtils.padLeft("abc", 6));
    }
}

