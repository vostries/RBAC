import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuditLogTest {

    @Test
    void logAndGetAll() {
        AuditLog log = new AuditLog();
        log.log("ACTION", "user1", "target1", "details");
        List<AuditLog.AuditEntry> all = log.getAll();
        assertEquals(1, all.size());
        AuditLog.AuditEntry e = all.get(0);
        assertEquals("ACTION", e.action());
        assertEquals("user1", e.performer());
        assertEquals("target1", e.target());
        assertEquals("details", e.details());
        assertNotNull(e.timestamp());
    }

    @Test
    void getByPerformerAndAction() {
        AuditLog log = new AuditLog();
        log.log("CREATE", "alice", "u1", "");
        log.log("DELETE", "bob", "u2", "");
        assertEquals(1, log.getByPerformer("alice").size());
        assertEquals(1, log.getByAction("CREATE").size());
        assertEquals(0, log.getByAction("UPDATE").size());
    }
}

