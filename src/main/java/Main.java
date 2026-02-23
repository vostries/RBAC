import java.util.List;

class Main {
    public static void main(String[] args) {
        System.out.println("1.1 User");
        User u = User.create("john_doe", "John Doe", "john@example.com");
        System.out.println(u.format());

        System.out.println("\n1.2 Permission");
        Permission p1 = new Permission("read", "users", "Просмотр списка пользователей");
        Permission p2 = new Permission("write", "users", "Создание и редактирование");
        System.out.println(p1.format());
        System.out.println("matches(read, user): " + p1.matches("read", "user"));

        System.out.println("\n1.3 Role");
        Role role = new Role("Administrator", "Полный доступ");
        role.addPermission(p1);
        role.addPermission(p2);
        System.out.println(role.format());
        System.out.println("hasPermission(write, users): " + role.hasPermission("write", "users"));

        System.out.println("\n1.4 AssignmentMetadata");
        AssignmentMetadata meta = AssignmentMetadata.now("admin", "Начальная настройка");
        System.out.println(meta.format());

        System.out.println("\n1.7 PermanentAssignment");
        PermanentAssignment pa = new PermanentAssignment(u, role, meta);
        System.out.println(pa.summary());
        pa.revoke();
        System.out.println("После revoke: " + pa.isRevoked() + ", isActive: " + pa.isActive());

        System.out.println("\n1.7 TemporaryAssignment");
        TemporaryAssignment ta = new TemporaryAssignment(u, role, meta, "2026-12-31 23:59");
        System.out.println(ta.summary());
        System.out.println("isActive: " + ta.isActive() + ", isExpired: " + ta.isExpired());
        System.out.println("getTimeRemaining: " + ta.getTimeRemaining());
        ta.extend("2025-01-01");
        System.out.println("После extend(2025-01-01): isExpired=" + ta.isExpired() + ", " + ta.getTimeRemaining());

        demoFiltersAndSorters();
    }

    static void demoFiltersAndSorters() {
        var perm = new Permission("read", "users", "Просмотр");
        var users = List.of(
            User.create("john_doe", "John Doe", "john@company.com"),
            User.create("jane_admin", "Jane Admin", "jane@company.com"),
            User.create("bob_user", "Bob User", "bob@gmail.com"));
        var adminRole = new Role("Administrator", "");
        adminRole.addPermission(perm);
        var viewerRole = new Role("Viewer", "");
        viewerRole.addPermission(perm);
        var roles = List.of(adminRole, viewerRole);

        System.out.println("\n2.1 UserFilter");
        var f1 = UserFilters.byUsernameContains("john").or(UserFilters.byEmailDomain("gmail.com"));
        users.stream().filter(f1::test).map(User::format).forEach(System.out::println);

        System.out.println("\n2.2 RoleFilter");
        var rf = RoleFilters.byNameContains("Admin").and(RoleFilters.hasAtLeastNPermissions(1));
        roles.stream().filter(rf::test).map(Role::getName).forEach(System.out::println);

        System.out.println("\n2.3 AssignmentFilter");
        var meta = AssignmentMetadata.now("admin", "test");
        var assignments = List.<RoleAssignment>of(
                new PermanentAssignment(users.get(0), adminRole, meta),
                new TemporaryAssignment(users.get(1), adminRole, meta, "2025-06-01"));
        assignments.stream().filter(AssignmentFilters.activeOnly()::test).map(RoleAssignment::assignmentId).forEach(System.out::println);

        System.out.println("\n2.4 Sorters");
        users.stream().sorted(UserSorters.byEmail()).map(User::username).forEach(System.out::println);
        roles.stream().sorted(RoleSorters.byPermissionCount()).map(Role::getName).forEach(System.out::println);
    }
}
