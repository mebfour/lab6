package Commands;



import Collection.RouteCollectionManager;
import Commands.Ordin.ExitCommand;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandProcessor {
    public CommandProcessor(RouteCollectionManager manager) {

    }
    private final Map<String, Command> commands = new HashMap<>();

    /**
     * Регистрирует новую команду.
     */
    public void register(ExitCommand command) {
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
    public CommandResponse process(CommandRequest request) {
        Command command = commands.get(request.getCommandName());

        if (command == null) {
            return new CommandResponse("Неизвестная команда", false);
        }
        return command.execute(request.getArgs());
    }

}
