import java.time.LocalDateTime;

class RBACSystem {
    private final UserManager userManager;
    private final RoleManager roleManager;
    private final AssignmentManager assignmentManager;
    private final AuditLog auditLog;
    private String currentUser;

    RBACSystem() {
        this.userManager = new UserManager();
        this.roleManager = new RoleManager();
        this.assignmentManager = new AssignmentManager(userManager, roleManager);
        this.auditLog = new AuditLog();
        roleManager.setRemoveGuard(role -> !assignmentManager.findByRole(role).isEmpty());
    }

    UserManager getUserManager() {
        return userManager;
    }

    RoleManager getRoleManager() {
        return roleManager;
    }

    AssignmentManager getAssignmentManager() {
        return assignmentManager;
    }

    AuditLog getAuditLog() {
        return auditLog;
    }

    void setCurrentUser(String username) {
        this.currentUser = username;
    }

    String getCurrentUser() {
        return currentUser != null ? currentUser : "system";
    }

    void initialize() {
        Permission readUsers = new Permission("READ", "users", "Просмотр пользователей");
        Permission writeUsers = new Permission("WRITE", "users", "Создание и редактирование пользователей");
        Permission deleteUsers = new Permission("DELETE", "users", "Удаление пользователей");
        Permission readRoles = new Permission("READ", "roles", "Просмотр ролей");
        Permission writeRoles = new Permission("WRITE", "roles", "Управление ролями");
        Permission readReports = new Permission("READ", "reports", "Просмотр отчётов");
        Permission writeReports = new Permission("WRITE", "reports", "Создание отчётов");

        Role admin = new Role("Admin", "Полный доступ к системе");
        admin.addPermission(readUsers);
        admin.addPermission(writeUsers);
        admin.addPermission(deleteUsers);
        admin.addPermission(readRoles);
        admin.addPermission(writeRoles);
        admin.addPermission(readReports);
        admin.addPermission(writeReports);
        roleManager.add(admin);

        Role manager = new Role("Manager", "Управление пользователями и просмотр отчётов");
        manager.addPermission(readUsers);
        manager.addPermission(writeUsers);
        manager.addPermission(readReports);
        roleManager.add(manager);

        Role viewer = new Role("Viewer", "Только просмотр данных");
        viewer.addPermission(readUsers);
        viewer.addPermission(readRoles);
        viewer.addPermission(readReports);
        roleManager.add(viewer);

        User adminUser = User.create("admin", "System Administrator", "admin@system.local");
        userManager.add(adminUser);

        AssignmentMetadata meta = AssignmentMetadata.now("system", "Инициализация системы");
        PermanentAssignment pa = new PermanentAssignment(adminUser, admin, meta);
        assignmentManager.add(pa);

        setCurrentUser("admin");
        auditLog.log("INIT", "system", "admin", "Инициализация системы и назначение роли Admin");
    }

    String generateStatistics() {
        int totalUsers = userManager.count();
        int totalRoles = roleManager.count();
        int totalAssignments = assignmentManager.count();
        int activeAssignments = assignmentManager.getActiveAssignments().size();
        int expiredAssignments = assignmentManager.getExpiredAssignments().size();

        double avgRolesPerUser = totalUsers > 0 ? (double) totalAssignments / totalUsers : 0;

        var rolePopularity = new java.util.HashMap<String, Integer>();
        for (RoleAssignment a : assignmentManager.findAll()) {
            if (a.isActive()) {
                String rn = a.role().getName();
                rolePopularity.put(rn, rolePopularity.getOrDefault(rn, 0) + 1);
            }
        }
        var top3 = rolePopularity.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(3)
                .toList();

        StringBuilder sb = new StringBuilder();
        sb.append("=== Статистика системы RBAC ===\n");
        sb.append(String.format("Пользователей: %d\n", totalUsers));
        sb.append(String.format("Ролей: %d\n", totalRoles));
        sb.append(String.format("Назначений: %d (активных: %d, истёкших: %d)\n",
                totalAssignments, activeAssignments, expiredAssignments));
        sb.append(String.format("Среднее кол-во ролей на пользователя: %.2f\n", avgRolesPerUser));
        sb.append("Топ-3 популярных ролей:\n");
        if (top3.isEmpty()) {
            sb.append("  —\n");
        } else {
            int pos = 1;
            for (var e : top3) {
                sb.append(String.format("  %d. %s (%d назначений)\n", pos++, e.getKey(), e.getValue()));
            }
        }
        return sb.toString();
    }
}
