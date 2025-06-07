package ToStart;

import java.io.Serializable;

public class CommandRequest implements Serializable {
    private final String commandName;
    private final String args;
    private final String username;

    public CommandRequest(String commandName, String args, String username) {
        this.commandName = commandName;
        this.args = args;
        this.username = username;
    }

    public String getCommandName() {
        return commandName;
    }
    public String getArgs() {
        return args;
    }
    public String getUsername() {return username;}
}

