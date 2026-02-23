import java.util.Comparator;

final class AssignmentSorters {

    private AssignmentSorters() {}

    static Comparator<RoleAssignment> byUsername() {
        return Comparator.comparing(a -> a.user().username(), Comparator.nullsLast(String::compareTo));
    }

    static Comparator<RoleAssignment> byRoleName() {
        return Comparator.comparing(a -> a.role().getName(), Comparator.nullsLast(String::compareTo));
    }

    static Comparator<RoleAssignment> byAssignmentDate() {
        return Comparator.comparing(a -> a.metadata().assignedAt(), Comparator.nullsLast(String::compareTo));
    }
}
