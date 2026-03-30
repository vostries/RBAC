import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class Subtask6ConcurrencyTest {

    @Test
    void parallelFiltersAndParallelReportsWork() {
        UserManager userManager = new UserManager();
        RoleManager roleManager = new RoleManager();
        AssignmentManager assignmentManager = new AssignmentManager(userManager, roleManager);
        Role role = new Role("AdminParallel", "parallel test");
        role.addPermission(new Permission("READ", "users", "read users"));
        roleManager.add(role);

        for (int i = 0; i < 50; i++) {
            User user = User.create("user_" + i, "User " + i, "user_" + i + "@mail.com");
            userManager.add(user);
            assignmentManager.add(new PermanentAssignment(user, role, AssignmentMetadata.now("test", "seed")));
        }

        assertFalse(userManager.findByFilterParallel(UserFilters.byUsernameContains("user_1")).isEmpty());
        assertFalse(roleManager.findByFilterParallel(RoleFilters.byNameContains("Admin")).isEmpty());
        assertFalse(assignmentManager.findByFilterParallel(AssignmentFilters.activeOnly()).isEmpty());

        ReportGenerator generator = new ReportGenerator();
        String report = generator.generateUserReportParallel(userManager, assignmentManager);
        String matrix = generator.generatePermissionMatrixParallel(userManager, assignmentManager);
        assertTrue(report.contains("parallel"));
        assertTrue(matrix.contains("Username"));
    }

    @Test
    void asyncReportAndAsyncSaveWork() throws Exception {
        RBACSystem system = new RBACSystem();
        system.initialize();
        Path output = Path.of("target", "rbac-subtask6-snapshot.txt");

        try {
            String report = system.generateUserReportAsync().get(5, TimeUnit.SECONDS);
            assertTrue(report.contains("Отчёт по пользователям"));

            system.saveSnapshotAsync(output.toString()).get(5, TimeUnit.SECONDS);
            assertTrue(Files.exists(output));
            assertTrue(Files.exists(Path.of(output.toString() + ".audit.log")));
        } finally {
            system.shutdown();
            Files.deleteIfExists(output);
            Files.deleteIfExists(Path.of(output.toString() + ".audit.log"));
        }
    }

    @Test
    void auditLogQueueAndSchedulerDeactivateExpiredAssignments() throws Exception {
        RBACSystem system = new RBACSystem();
        system.initialize();
        User user = User.create("tmp_scheduler", "Tmp Scheduler", "tmp_scheduler@mail.com");
        system.getUserManager().add(user);
        Role role = new Role("TmpSchedulerRole", "temp");
        role.addPermission(new Permission("READ", "tmp", "temp"));
        system.getRoleManager().add(role);
        TemporaryAssignment temp = new TemporaryAssignment(
                user, role, AssignmentMetadata.now("test", "scheduler"), "2000-01-01 00:00");
        system.getAssignmentManager().add(temp);

        try {
            system.startExpiredAssignmentScheduler(1);
            system.getAuditLog().log("TEST", "user", "target", "details");
            Thread.sleep(1200);
            assertFalse(temp.isActive());
            assertFalse(system.getAuditLog().getAll().isEmpty());
        } finally {
            system.shutdown();
        }
    }

    @Test
    void loadTestManagersDoNotCrashAndKeepUniqueUsers() throws Exception {
        UserManager userManager = new UserManager();
        RoleManager roleManager = new RoleManager();
        AssignmentManager assignmentManager = new AssignmentManager(userManager, roleManager);
        Role loadRole = new Role("LoadRole", "load");
        loadRole.addPermission(new Permission("READ", "load", "load read"));
        roleManager.add(loadRole);

        int threads = 8;
        int iterations = 60;
        var pool = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        List<Throwable> errors = new ArrayList<>();

        for (int t = 0; t < threads; t++) {
            final int threadId = t;
            pool.submit(() -> {
                try {
                    for (int i = 0; i < iterations; i++) {
                        String username = "load_user_" + threadId + "_" + i;
                        User user = User.create(username, "Load User " + i, username + "@mail.com");
                        userManager.add(user);
                        userManager.update(username, "Updated " + i, username + "@mail.com");
                        assignmentManager.add(new PermanentAssignment(user, loadRole, AssignmentMetadata.now("load", "test")));
                        userManager.findByFilterParallel(UserFilters.byUsernameContains("load_user"));
                        assignmentManager.findByFilterParallel(AssignmentFilters.byRole(loadRole));
                    }
                } catch (Throwable e) {
                    synchronized (errors) {
                        errors.add(e);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS));
        pool.shutdownNow();
        assertTrue(errors.isEmpty(), "Ошибки в потоках: " + errors);
        assertEquals(threads * iterations, userManager.count());
    }
}
