package Commands;

import Collection.RouteCollectionManager;
import Commands.Ordin.ExitCommand;
import Commands.CommandList;
//import Commands.Ordin.HelpCommand;

/**
 * Для добавления команд в commandList
 */
public class CommandConfigurator {
    private static CommandConfigurator commandConfigurator;

    //  Не хотим создавать экземляры классы
    private CommandConfigurator() {}

    public static synchronized CommandList configureCommands(RouteCollectionManager manager){
        CommandList commandList = new CommandList();

        Runnable shutdownHook = new Runnable() {
            @Override
            public void run() {

            }
        };
        ExitCommand exit = new ExitCommand();
        commandList.register(exit);
        //commandList.register(new HelpCommand(commandList));
        return commandList;
    }
}
