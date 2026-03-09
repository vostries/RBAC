import java.util.Locale;

public record Permission(String name, String resource, String description) {

    public Permission(String name, String resource, String description) {
        ValidationUtils.requireNonEmpty(description, "Описание");

        String normalizedName = ValidationUtils.normalizeString(name);
        String normalizedResource = ValidationUtils.normalizeString(resource);

        if (normalizedName.isEmpty()) {
            throw new IllegalArgumentException("Имя права не может быть пустым");
        }
        if (normalizedResource.isEmpty()) {
            throw new IllegalArgumentException("Ресурс не может быть пустым");
        }

        this.name = normalizedName.toUpperCase(Locale.ROOT).replace(" ", "");
        this.resource = normalizedResource.toLowerCase(Locale.ROOT);
        this.description = description;
    }

    public String format() {
        return String.format("%s on %s: %s", name, resource, description);
    }

    public boolean matches(String namePattern, String resourcePattern) {
        if (namePattern == null || resourcePattern == null)
            return false;
        String n = namePattern.toUpperCase(Locale.ROOT).replace(" ", "");
        String r = resourcePattern.toLowerCase(Locale.ROOT);
        return name.contains(n) && resource.contains(r);
    }
}
