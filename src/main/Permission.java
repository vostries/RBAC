import java.util.Locale;

public record Permission(String name, String resource, String description) {

    public Permission(String name, String resource, String description) {
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Описание права не может быть пустым");
        if (name == null)
            name = "";
        if (resource == null)
            resource = "";
        this.name = name.toUpperCase(Locale.ROOT).replace(" ", "");
        this.resource = resource.toLowerCase(Locale.ROOT);
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
