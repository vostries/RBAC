public final class RoleFilters {

    private RoleFilters() {}

    static RoleFilter byName(String name) {
        return r -> r != null && name != null && r.getName().equals(name);
    }

    static RoleFilter byNameContains(String substring) {
        if (substring == null) return r -> false;
        String sub = substring.toLowerCase();
        return r -> r != null && r.getName().toLowerCase().contains(sub);
    }

    static RoleFilter hasPermission(Permission permission) {
        return r -> r != null && permission != null && r.hasPermission(permission);
    }

    static RoleFilter hasPermission(String permissionName, String resource) {
        return r -> r != null && r.hasPermission(permissionName, resource);
    }

    static RoleFilter hasAtLeastNPermissions(int n) {
        return r -> r != null && r.getPermissions().size() >= n;
    }
}
