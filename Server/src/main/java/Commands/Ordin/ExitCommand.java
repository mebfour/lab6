package Commands.Ordin;

import Collection.RouteCollectionManager;
import Server.Commands.Command;

public class ExitCommand implements Command {

    private final Runnable shutdownHook;


    private final RouteCollectionManager manager;
    public ExitCommand(RouteCollectionManager manager, Runnable shutdownHook) {
        this.shutdownHook = shutdownHook;
        this.manager = manager;
    }


    @Override
    public void execute(String args) {
        shutdownHook.run();
        return new CommandResponse(" ______\n" +
                "< bye >\n" +
                " ------\n" +
                "        \\   ^__^\n" +
                "         \\  (oo)\\_______\n" +
                "            (__)\\       )\\/\\\n" +
                "                ||----w |\n" +
                "                ||     ||\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n");

    }

    @Override
    public String getDescription() {
        return "завершает программу";
    }
    @Override
    public String getName(){
        return "exit";
    }
}
