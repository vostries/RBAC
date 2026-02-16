void main() {
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
}
