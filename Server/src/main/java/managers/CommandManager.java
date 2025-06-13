package managers;



import Collection.RouteCollectionManager;
import Commands.*;
import Commands.BaseCom.CheckIdCommand;
import Commands.BaseCom.GetRoutesCommand;
import Commands.BaseCom.InfoCommand;
import Commands.BaseCom.ShowCommand;
import Commands.Command;
import Commands.Elt.MaxByID;
import Commands.Elt.MinByName;
import Commands.Modif.AddCommand;
import Commands.Modif.Clear;
import Commands.Modif.*;
import Commands.Ordin.ExitCommand;
import Commands.Ordin.HelpCommand;
import users.LoginCommand;
import users.RegisterCommand;


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
        commandList.put("update_id", new UpdateId(collectionManager));
        commandList.put("max_by_id", new MaxByID());
        commandList.put("min_by_name", new MinByName());
        commandList.put("check_id", new CheckIdCommand());
        commandList.put("register", new RegisterCommand());
        commandList.put("login", new LoginCommand());
        commandList.put("get_routes", new GetRoutesCommand());
    }
    public static CommandResponse checkComm(CommandRequest request) {
        String commandName = request.getCommandName().toLowerCase();

        Command command = commandList.get(commandName);
        if (command != null) {
            System.out.println(request.getArgs());
            return command.execute(request.getArgs());
        } else {
            return new CommandResponse("Такой команды нет, давайте попробуем другой набор символов", false);
        }
    }

}


