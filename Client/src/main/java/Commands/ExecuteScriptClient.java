package Commands;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ExecuteScriptClient implements ClientCommand<List<ExecuteScriptClient.Pair<String, String[]>>> {
    private static final HashSet<String> callStack = new HashSet<>();

    @Override
    public List<Pair<String, String[]>> clientExecute(String[] args) {
        List<Pair<String, String[]>> scriptCommands = new ArrayList<>();
        String filePath = (args.length > 0 && args[0] != null && !args[0].trim().isEmpty())
                ? args[0].trim()
                : askFilePath();

        if (!new java.io.File(filePath).exists()) {
            System.out.println("Файл не найден: " + filePath);
            return scriptCommands;
        }
        if (callStack.contains(filePath)) {
            System.out.println("Рекурсивный вызов скрипта обнаружен. Скрипт не будет выполнен повторно: " + filePath);
            return scriptCommands;
        }

        callStack.add(filePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+", 2);
                String commandName = parts[0];
                String[] cmdArgs = (parts.length > 1) ? new String[]{parts[1]} : new String[0];

                // Рекурсивная обработка execute_script
                if (commandName.equals("execute_script")) {
                    if (callStack.contains(cmdArgs[0])) {
                        System.out.println("Рекурсивный вызов скрипта обнаружен: " + cmdArgs[0]);
                        continue;
                    }
                    // Рекурсивно собираем команды из вложенного скрипта
                    List<Pair<String, String[]>> nested = new ExecuteScriptClient().clientExecute(cmdArgs);
                    scriptCommands.addAll(nested);
                    continue;
                }

                scriptCommands.add(new Pair<>(commandName, cmdArgs));
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла");
        } finally {
            callStack.remove(filePath);
        }
        return scriptCommands;
    }

    private String askFilePath() {
        System.out.print("Введите путь к файлу: ");
        return new java.util.Scanner(System.in).nextLine().trim();
    }

    // Простая реализация Pair, если у вас нет своей
    public static class Pair<K, V> {
        public final K key;
        public final V value;
        public Pair(K k, V v) { key = k; value = v; }
    }
}
