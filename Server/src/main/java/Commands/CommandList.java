package Server.Commands;
import java.util.*;

/**
 * Хранит все доступные команды и позволяет их искать по имени.
 */
public class CommandList {
    private final Map<String, Command> commands = new HashMap<>();

    /**
     * Регистрирует новую команду.
     */
    public void register(Command command) {
        commands.put(command.getName().toLowerCase(), command);
    }

    /**
     * Возвращает команду по имени или null, если не найдена.
     */
    public Command get(String name) {
        return commands.get(name.toLowerCase());
    }

    /**
     * Возвращает все зарегистрированные команды.
     */
    public Collection<Command> getAll() {
        return commands.values();
    }
}