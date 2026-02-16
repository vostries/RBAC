public record User(String username, String fullName, String email) {

    public static User create(String username, String fullName, String email) {
        if (username == null || fullName == null || email == null)
            throw new IllegalArgumentException("Все поля обязательны");

        if (username.isBlank() || fullName.isBlank() || email.isBlank())
            throw new IllegalArgumentException("Строки не могут быть пустыми");

        if (username.length() < 3 || username.length() > 20)
            throw new IllegalArgumentException("Длина username от 3 до 20 символов");

        if (!username.matches("^[a-zA-Z0-9_]+$"))
            throw new IllegalArgumentException("username: только латиница, цифры и подчёркивание");

        int at = email.indexOf('@');
        if (at <= 0 || !email.substring(at).contains("."))
            throw new IllegalArgumentException("Некорректный email: нужны @ и точка после @");

        return new User(username, fullName, email);
    }

    public String format() {
        return String.format("%s (%s) <%s>", username, fullName, email);
    }
}
