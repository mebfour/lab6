package Commands;
import com.google.gson.Gson;
import java.nio.channels.SocketChannel;
import java.util.*;

import java.util.function.Consumer;

public class ClientCommandList implements Iterable<ClientCommand> {
    private final List<ClientCommand> commands;

    private ClientCommandList(List<ClientCommand> commands) {
        this.commands = commands;
    }

    public static ClientCommandList create(SocketChannel socketChannel,
                                           Gson gson,
                                           Consumer<String> sendMessage,
                                           IdChecker idChecker
                                           ) {

        List<ClientCommand> commands = List.of(
                new ExitCommand(socketChannel),
                new register(gson, sendMessage),
                new AddCommand(gson, sendMessage),
                new InsertWithKeyCommand(gson, sendMessage),
                new RemoveByKeyCommand(gson, sendMessage),
                new RemoveGreaterCommand(gson, sendMessage),
                new RemoveLowerCommand(gson, sendMessage),
                new ReplaceIfLoweCommand(gson, sendMessage),
                new UpdateIdCommand(gson, sendMessage, idChecker),
                new ExecuteScriptCommand(gson, sendMessage, new ArrayList<>(), new HashSet<>())
        );

        return new ClientCommandList(commands);
    }


    @Override
    public Iterator<ClientCommand> iterator() {
        return commands.iterator();
    }
}