package Commands;

import Commands.Ordin.ExitCommand;

public class CommandProcessor {
    public CommandResponse process(String commandName) {
        if ("exit".equals(commandName)) {
            return new ExitCommand(this::shutdown).execute();
        }
        // ... обработка других команд
    }
}
