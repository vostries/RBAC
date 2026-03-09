public record User(String username, String fullName, String email) {

    public static User create(String username, String fullName, String email) {
        if (username == null || fullName == null || email == null) {
            throw new IllegalArgumentException("Все поля обязательны");
        }

        ValidationUtils.requireNonEmpty(username, "username");
        ValidationUtils.requireNonEmpty(fullName, "fullName");
        ValidationUtils.requireNonEmpty(email, "email");

        if (!ValidationUtils.isValidUsername(username)) {
            throw new IllegalArgumentException("Длина username от 3 до 20 символов, допустимы только латиница, цифры и подчёркивание");
        }

        if (!ValidationUtils.isValidEmail(email)) {
            throw new IllegalArgumentException("Некорректный email");
        }

        return new User(username, fullName, email);
    }

    public String format() {
        return String.format("%s (%s) <%s>", username, fullName, email);
    }
}
