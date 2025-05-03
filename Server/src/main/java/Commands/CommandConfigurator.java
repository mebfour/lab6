package Server.Commands;

import CollectionManagement.RouteCollectionManager;
import Server.Commands.Ordin.ExitCommand;
import Server.Commands.Ordin.HelpCommand;

/**
 * Для добавления команд в commandList
 */
public class CommandConfigurator {
    private static CommandConfigurator commandConfigurator;

    //  Не хотим создавать экземляры классы
    private CommandConfigurator() {}

    public static synchronized CommandList configureCommands(RouteCollectionManager manager){
        CommandList commandList = new CommandList();

        commandList.register(new ExitCommand(manager));
        commandList.register(new HelpCommand(commandList));
        return commandList;
    }
}
