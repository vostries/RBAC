import java.util.Comparator;

public final class RoleSorters {

    private RoleSorters() {}

    static Comparator<Role> byName() {
        return Comparator.comparing(Role::getName, Comparator.nullsLast(String::compareTo));
    }

    static Comparator<Role> byPermissionCount() {
        return Comparator.comparingInt(r -> r.getPermissions().size());
    }
}
