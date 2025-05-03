package Server.Commands.Ordin;

import CollectionManagement.RouteCollectionManager;
import Server.Commands.Command;
import Server.Commands.CommandList;

public class HelpCommand implements Command {
    private final CommandList commandList;

    public HelpCommand(CommandList commandList){this.commandList = commandList;}
    @Override
    public void execute(String args) {
        System.out.println("Доступные команды:");

        commandList.getAll().forEach(cmd ->
                System.out.printf("  %-15s %s%n", cmd.getName(), cmd.getDescription()));
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
