package managers;



import Collection.RouteCollectionManager;
import Commands.*;
import Commands.BaseCom.InfoCommand;
import Commands.BaseCom.ShowCommand;
import Commands.Command;
import Commands.Modif.AddCommand;
import Commands.Modif.Clear;
import Commands.Modif.*;
import Commands.Ordin.ExitCommand;
import Commands.Ordin.HelpCommand;


import java.util.HashMap;
import java.util.Map;


public class CommandManager {
    public static Map<String, Command> commandList = new HashMap<>();
    public static RouteCollectionManager collectionManager = new RouteCollectionManager();
    static {

        commandList.put("exit", new ExitCommand());
        commandList.put("add", new AddCommand(collectionManager));
        commandList.put("info", new InfoCommand());
        commandList.put("show", new ShowCommand());
        commandList.put("help", new HelpCommand());
        commandList.put("remove_by_key", new RemoveByKey());
        commandList.put("clear", new Clear());
        commandList.put("remove_greater", new RemoveGreater());
        commandList.put("remove_lower", new RemoveLower());
        commandList.put("replace_if_lowe", new ReplaceIfLowe(collectionManager));
//        commandList.put("execute_script", new ExecuteScript());

    }
    public static CommandResponse checkComm(CommandRequest request) {
        String commandName = request.getCommandName().toLowerCase();

        Command command = commandList.get(commandName);
        if (command != null) {

            return command.execute(request.getArgs());
        } else {
            return new CommandResponse("Такой команды нет, давайте попробуем другой набор символов", false);
        }
    }

}


