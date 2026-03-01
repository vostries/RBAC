import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

class CommandParser {
    private final Map<String, Command> commands = new LinkedHashMap<>();
    private final Map<String, String> commandDescriptions = new LinkedHashMap<>();

    void registerCommand(String name, String description, Command command) {
        commands.put(name, command);
        commandDescriptions.put(name, description);
    }

    void executeCommand(String commandName, Scanner scanner, RBACSystem system) {
        Command cmd = commands.get(commandName);
        if (cmd == null) {
            System.out.println("Неизвестная команда: " + commandName);
            System.out.println("Введите 'help' для списка команд.");
            return;
        }
        try {
            cmd.execute(scanner, system);
        } catch (Exception e) {
            System.out.println("Ошибка выполнения команды: " + e.getMessage());
        }
    }

    void printHelp() {
        System.out.println("\n=== Список команд ===");
        for (var entry : commandDescriptions.entrySet()) {
            System.out.printf("  %-25s %s\n", entry.getKey(), entry.getValue());
        }
        System.out.println();
    }

    void parseAndExecute(String input, Scanner scanner, RBACSystem system) {
        if (input == null || input.isBlank()) {
            return;
        }
        String[] parts = input.trim().split("\\s+", 2);
        String commandName = parts[0].toLowerCase();
        executeCommand(commandName, scanner, system);
    }
}
