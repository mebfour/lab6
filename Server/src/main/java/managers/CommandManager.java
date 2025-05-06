package managers;



import Collection.RouteCollectionManager;
import Commands.*;
//import Commands.ForScript.ExecuteScript;
//import Commands.XmlProcessing.SaveCommand;
import Commands.Command;
import Commands.Ordin.ExitCommand;
import Commands.Ordin.HelpCommand;


import java.util.HashMap;
import java.util.Map;


public class CommandManager {
    public static Map<String, Command> commandList = new HashMap<>();

    static {

        commandList.put("exit", new ExitCommand());
//        commandList.put("add", new AddCommand());
//        commandList.put("info", new InfoCommand());
//        commandList.put("show", new ShowCommand());
//        commandList.put("insert_with_key", new InsertWithKey());
//        commandList.put("update_id", new UpdateID());
        commandList.put("help", new HelpCommand());
//        commandList.put("remove_by_key", new RemoveByKey());
//        commandList.put("clear", new Clear());
//        commandList.put("remove_greater", new RemoveGreater());
//        commandList.put("remove_lower", new RemoveLower());
//        commandList.put("replace_if_lowe", new ReplaceIfLowe());
//        commandList.put("min_by_name", new MinByName());
//        commandList.put("max_by_id", new MaxByID());
//        commandList.put("save", new SaveCommand());
//        commandList.put("execute_script", new ExecuteScript());
//        commandList.put("filter_greater_than_distance", new FilterGreaterThanDistance());
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


