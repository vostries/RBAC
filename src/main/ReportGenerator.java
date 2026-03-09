import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

final class ReportGenerator {

    String generateUserReport(UserManager userManager, AssignmentManager assignmentManager) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Отчёт по пользователям ===\n");
        List<User> users = new ArrayList<>(userManager.findAll());
        users.sort(Comparator.comparing(User::username));
        if (users.isEmpty()) {
            sb.append("Пользователей нет.\n");
            return sb.toString();
        }
        for (User u : users) {
            sb.append(String.format("Пользователь: %s (%s) <%s>%n",
                    u.username(), u.fullName(), u.email()));
            List<RoleAssignment> assignments = assignmentManager.findByUser(u);
            if (assignments.isEmpty()) {
                sb.append("  Ролей нет\n");
            } else {
                sb.append("  Роли:\n");
                for (RoleAssignment a : assignments) {
                    sb.append(String.format("    - %s [%s, %s]%n",
                            a.role().getName(),
                            a.assignmentType(),
                            a.isActive() ? "ACTIVE" : "INACTIVE"));
                }
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    String generateRoleReport(RoleManager roleManager, AssignmentManager assignmentManager) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Отчёт по ролям ===\n");
        List<Role> roles = new ArrayList<>(roleManager.findAll());
        roles.sort(Comparator.comparing(Role::getName));
        if (roles.isEmpty()) {
            sb.append("Ролей нет.\n");
            return sb.toString();
        }
        for (Role r : roles) {
            List<RoleAssignment> forRole = assignmentManager.findByRole(r);
            long active = forRole.stream().filter(RoleAssignment::isActive).count();
            sb.append(String.format("Роль: %s (ID: %s)%n", r.getName(), r.getId()));
            sb.append(String.format("  Пользователей всего: %d, активных назначений: %d%n",
                    forRole.size(), active));
            sb.append(String.format("  Кол-во прав: %d%n", r.getPermissions().size()));
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    String generatePermissionMatrix(UserManager userManager, AssignmentManager assignmentManager) {
        List<User> users = new ArrayList<>(userManager.findAll());
        users.sort(Comparator.comparing(User::username));
        StringBuilder sb = new StringBuilder();
        sb.append("=== Матрица прав (пользователь × ресурс) ===\n");
        if (users.isEmpty()) {
            sb.append("Пользователей нет.\n");
            return sb.toString();
        }
        // Собираем множество ресурсов
        Set<String> resources = new LinkedHashSet<>();
        for (User u : users) {
            for (Permission p : assignmentManager.getUserPermissions(u)) {
                resources.add(p.resource());
            }
        }
        List<String> resourceList = new ArrayList<>(resources);
        resourceList.sort(String::compareTo);
        if (resourceList.isEmpty()) {
            sb.append("Прав нет.\n");
            return sb.toString();
        }
        // Заголовок
        sb.append(String.format("%-20s", "Username"));
        for (String res : resourceList) {
            sb.append(String.format(" %-10s", res));
        }
        sb.append(System.lineSeparator());
        // Разделитель
        int width = 20 + resourceList.size() * 11;
        sb.append("-".repeat(width)).append(System.lineSeparator());
        // Строки
        for (User u : users) {
            sb.append(String.format("%-20s", u.username()));
            Set<Permission> perms = assignmentManager.getUserPermissions(u);
            for (String res : resourceList) {
                boolean has = perms.stream().anyMatch(p -> p.resource().equals(res));
                sb.append(String.format(" %-10s", has ? "X" : ""));
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    void exportToFile(String report, String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Имя файла отчёта не указано");
        }
        Path path = Path.of(filename.trim());
        try {
            Files.writeString(path, report == null ? "" : report);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить отчёт: " + e.getMessage(), e);
        }
    }
}

