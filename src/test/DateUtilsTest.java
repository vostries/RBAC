import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    void isBeforeAndAfter() {
        assertTrue(DateUtils.isBefore("2024-01-01", "2024-01-02"));
        assertTrue(DateUtils.isAfter("2024-01-03", "2024-01-02"));
        assertFalse(DateUtils.isBefore("2024-01-02", "2024-01-02"));
    }

    @Test
    void addDaysWorks() {
        String d = DateUtils.addDays("2024-01-01", 5);
        assertEquals("2024-01-06", d);
    }

    @Test
    void formatRelativeTimeTodayOrFuturePast() {
        String today = DateUtils.getCurrentDate();
        assertEquals("today", DateUtils.formatRelativeTime(today));
    }
}

