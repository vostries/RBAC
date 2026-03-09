import java.util.List;
import java.util.Scanner;

final class ConsoleUtils {

    private ConsoleUtils() {
    }

    static String promptString(Scanner scanner, String message, boolean required) {
        while (true) {
            System.out.print(message);
            String value = scanner.nextLine();
            if (!required || (value != null && !value.isBlank())) {
                return value != null ? value : "";
            }
            System.out.println("Значение не может быть пустым, попробуйте ещё раз.");
        }
    }

    static int promptInt(Scanner scanner, String message, int min, int max) {
        while (true) {
            System.out.print(message);
            String line = scanner.nextLine();
            try {
                int value = Integer.parseInt(line.trim());
                if (value < min || value > max) {
                    System.out.printf("Введите число в диапазоне [%d, %d].%n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Ожидается целое число, попробуйте ещё раз.");
            }
        }
    }

    static boolean promptYesNo(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String line = scanner.nextLine();
            if (line == null) {
                continue;
            }
            String v = line.trim().toLowerCase();
            if (v.equals("да") || v.equals("y") || v.equals("yes")) {
                return true;
            }
            if (v.equals("нет") || v.equals("n") || v.equals("no")) {
                return false;
            }
            System.out.println("Ответьте 'да' или 'нет'.");
        }
    }

    static <T> T promptChoice(Scanner scanner, String message, List<T> options) {
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("Список вариантов пуст");
        }
        System.out.println(message);
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
        int choice = promptInt(scanner, "Выберите номер: ", 1, options.size());
        return options.get(choice - 1);
    }
}

