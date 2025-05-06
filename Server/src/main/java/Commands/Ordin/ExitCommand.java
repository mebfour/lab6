package Commands.Ordin;

import Collection.RouteCollectionManager;
import Commands.CommandResponse;
import Commands.Command;


public class ExitCommand implements Command {


    public ExitCommand() {

    }



    @Override
    public CommandResponse execute (String args) {
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
                "\n", true);

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
