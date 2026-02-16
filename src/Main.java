void main() {
    System.out.println(User.create("john_doe", "John Doe", "john@example.com").format());

    try {
        User.create("ab", "Full", "a@b.c");
    } catch (IllegalArgumentException e) {
        System.out.println("Ожидаемо: " + e.getMessage());
    }
    try {
        User.create("user!", "Full", "a@b.c");
    } catch (IllegalArgumentException e) {
        System.out.println("Ожидаемо: " + e.getMessage());
    }
    try {
        User.create("valid", "Full", "badmail");
    } catch (IllegalArgumentException e) {
        System.out.println("Ожидаемо: " + e.getMessage());
    }
}
