import java.io.Serializable;

public class CommandRequest implements Serializable {
    private final String commandName;
    private final String args;

    public CommandRequest(String commandName, String args) {
        this.commandName = commandName;
        this.args = args;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getArgs() {
        return args;
    }
}

