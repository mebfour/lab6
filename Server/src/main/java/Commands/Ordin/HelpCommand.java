package Commands.Ordin;

import Collection.RouteCollectionManager;
import Commands.CommandResponse;
import Commands.Command;
import Commands.CommandList;

import java.util.Map;

import static Collection.RouteCollectionManager.routeList;
import static managers.CommandManager.commandList;

public class HelpCommand implements Command {



    @Override
    public CommandResponse execute(String args) {
        System.out.println("Доступные команды:");
        String ans ="";
        synchronized(routeList) {
            for (Map.Entry<String, Command> currentCommand : commandList.entrySet()) {
                String commandName = currentCommand.getKey();
                Command command = commandList.get(commandName);
                ans += ("- " + commandName + " " + command.getDescription() + '\n');
            }
        }
        return new CommandResponse(ans, true);
    }


    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "выводит справку по доступным командам";
    }
}
