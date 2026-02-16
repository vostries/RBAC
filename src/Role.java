import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Role {
    private final String id;
    private final String name;
    private final String description;
    private Set<Permission> permissions = new HashSet<>();

    public Role(String name, String description) {
        this.id = "role_" + UUID.randomUUID();
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<Permission> getPermissions() {
        return Set.copyOf(permissions);
    }

    public void addPermission(Permission permission) {
        permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        permissions.remove(permission);
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    public boolean hasPermission(String permissionName, String resource) {
        if (permissionName == null || resource == null)
            return false;
        return permissions.stream().anyMatch(p -> p.matches(permissionName, resource));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return id.equals(role.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Role{id='" + id + "', name='" + name + "', description='" + description + "', permissions=" + permissions + "}";
    }

    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Role: %s [ID: %s]\n", name, id));
        sb.append(String.format("Description: %s\n", description));
        sb.append(String.format("Permissions (%d):\n", permissions.size()));
        if (permissions.isEmpty()) {
            sb.append("  Нет назначенных прав\n");
        } else {
            for (Permission p : permissions) {
                sb.append("  - ").append(p.format()).append("\n");
            }
        }
        return sb.toString();
    }
}
