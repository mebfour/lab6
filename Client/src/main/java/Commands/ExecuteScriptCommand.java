package Commands;

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
    public void clientExecute(String[] args, String pars) throws IOException {

        String filePath = args[0];
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("мы тут");
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
                            .clientExecute(args, pars);

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
