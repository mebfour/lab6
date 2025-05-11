package Commands;


import com.google.gson.Gson;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientCommandList {
    private final Map<String, ClientCommand> commandMap = new HashMap<>();
    private final Gson gson = new Gson();
    private SocketChannel socketChannel;
    private Selector selector;
    private final Queue<ByteBuffer> writeQueue = new ConcurrentLinkedQueue<>();



    public void execute(String commandName, String[] args) throws IOException {
        ClientCommand command = commandMap.get(commandName.toLowerCase());
        if (command != null) {
            command.clientExecute(args);
        } else {
            System.out.println("Неизвестная команда: " + commandName);
        }
    }




}

