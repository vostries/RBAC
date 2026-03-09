import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReportGeneratorTest {

    UserManager userManager;
    RoleManager roleManager;
    AssignmentManager assignmentManager;
    ReportGenerator generator;

    @BeforeEach
    void setUp() {
        userManager = new UserManager();
        roleManager = new RoleManager();
        assignmentManager = new AssignmentManager(userManager, roleManager);
        generator = new ReportGenerator();

        User u = User.create("john", "John Doe", "john@mail.com");
        userManager.add(u);
        Role r = new Role("Admin", "Админ");
        r.addPermission(new Permission("read", "users", "Просмотр"));
        roleManager.add(r);
        AssignmentMetadata meta = AssignmentMetadata.now("system", "test");
        assignmentManager.add(new PermanentAssignment(u, r, meta));
    }

    @Test
    void userReportContainsUserAndRole() {
        String report = generator.generateUserReport(userManager, assignmentManager);
        assertTrue(report.contains("john"));
        assertTrue(report.contains("Admin"));
    }

    @Test
    void roleReportContainsRoleAndCounts() {
        String report = generator.generateRoleReport(roleManager, assignmentManager);
        assertTrue(report.contains("Роль: Admin"));
        assertTrue(report.contains("Пользователей всего: 1"));
    }

    @Test
    void permissionMatrixHasHeaderAndUserRow() {
        String report = generator.generatePermissionMatrix(userManager, assignmentManager);
        assertTrue(report.contains("Username"));
        assertTrue(report.contains("john"));
    }
}

