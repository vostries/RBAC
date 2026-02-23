import java.util.UUID;

public abstract class AbstractRoleAssignment implements RoleAssignment {
    private final String assignmentId;
    private final User user;
    private final Role role;
    private final AssignmentMetadata metadata;

    public AbstractRoleAssignment(User user, Role role, AssignmentMetadata metadata) {
        this.assignmentId = "assignment_" + UUID.randomUUID();
        this.user = user;
        this.role = role;
        this.metadata = metadata;
    }

    public abstract boolean isActive();
    public abstract String assignmentType();

    @Override
    public String assignmentId() {
        return assignmentId;
    }

    @Override
    public User user() {
        return user;
    }

    @Override
    public Role role() {
        return role;
    }

    @Override
    public AssignmentMetadata metadata() {
        return metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractRoleAssignment that = (AbstractRoleAssignment) o;
        return assignmentId.equals(that.assignmentId);
    }

    @Override
    public int hashCode() {
        return assignmentId.hashCode();
    }

    public String summary() {
        String reason = metadata.reason() != null && !metadata.reason().isBlank() ? metadata.reason() : "—";
        return "[" + assignmentType() + "] " + role.getName() + " назначена пользователю " + user.username()
                + ", назначил " + metadata.assignedBy() + " " + metadata.assignedAt() + "\n"
                + "Причина: " + reason + "\n"
                + "Статус: " + (isActive() ? "ACTIVE" : "INACTIVE");
    }
}
