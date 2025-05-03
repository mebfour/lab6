package Server.Commands;

import Exceptions.CommandException;

/**
 * Только выполняет команды из CommandList.
 */
public class CommandExecutor {
    private final CommandList commandList;

    public CommandExecutor(CommandList commandList) {
        this.commandList = commandList;
    }

    /**
     * Выполняет команду из строки ввода.
     */
    public void execute(String input) throws CommandException {
        if (input == null || input.isEmpty()) {
            return;
        }

        String[] parts = input.split(" ", 2);   //  делаем массив из 1-2х элементов
        String commandName = parts[0];
        String args = parts.length > 1 ? parts[1] : null;

        Command command = commandList.get(commandName);
        if (command == null) {
            throw new CommandException("Команда не найдена: " + commandName);
        }

        try {
            command.execute(args);
        } catch (Exception e) {
            throw new CommandException(
                    "Ошибка выполнения '" + commandName + "': " + e.getMessage()
            );
        }
    }
}