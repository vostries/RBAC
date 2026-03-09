import java.util.Comparator;

public final class UserSorters {

    private UserSorters() {}

    static Comparator<User> byUsername() {
        return Comparator.comparing(User::username, Comparator.nullsLast(String::compareTo));
    }

    static Comparator<User> byFullName() {
        return Comparator.comparing(User::fullName, Comparator.nullsLast(String::compareTo));
    }

    static Comparator<User> byEmail() {
        return Comparator.comparing(User::email, Comparator.nullsLast(String::compareTo));
    }
}
