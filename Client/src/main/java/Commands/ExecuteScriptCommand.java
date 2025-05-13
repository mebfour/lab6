package Commands;

import InputHandler.InputProvider;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class ExecuteScriptCommand implements ClientCommand {

    private final HashSet<String> callStack;
    private final Gson gson;
    private final Consumer<String> sendMessage;
    private final List<Pair<String, String[]>> scriptCommands;

    public ExecuteScriptCommand(Gson gson, Consumer<String> sendMessage, List<Pair<String, String[]>> scriptCommands,HashSet<String> callStack) {
        this.gson = gson;
        this.sendMessage = sendMessage;
        this.scriptCommands = scriptCommands;
        this.callStack = callStack;
    }

    @Override
    public void clientExecute(String[] args, String pars, InputProvider provider, Scanner scanner) throws IOException {

        String filePath = (args.length > 1 && args[0] != null && !args[0].trim().isEmpty())
                ? String.join(" ", Arrays.asList(args).subList(1, args.length))
                : askFilePath();

        if (!new java.io.File(filePath).exists()) {
            System.out.println("Файл не найден: " + filePath);

        }
        if (callStack.contains(filePath)) {
            System.out.println("Рекурсивный вызов скрипта обнаружен. Скрипт не будет выполнен повторно: " + filePath);
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
                    new ExecuteScriptCommand(gson, sendMessage, scriptCommands, callStack)
                            .clientExecute(args, pars, provider, scanner);

                    callStack.remove(filePath);
                    continue;
                }

                scriptCommands.add(new Pair<>(commandName, cmdArgs));
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла");
        } finally {
            callStack.remove(filePath);
        }
    }


    private String askFilePath() {
        System.out.print("Введите путь к файлу: ");
        return new java.util.Scanner(System.in).nextLine().trim();
    }


    public static class Pair<K, V> {
        public final K key;
        public final V value;
        public Pair(K k, V v) { key = k; value = v; }
    }

    @Override
    public String getName() {
        return "execute_script";
    }
}
