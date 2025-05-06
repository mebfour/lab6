package Commands;

import Classes.Route;
import InputHandler.KeyboardInputProvider;
import InputHandler.inputObject;
import ToStart.CommandRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
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
    public ClientCommandList() {
        commandMap.put("help", new HelpCommand());
        commandMap.put("exit", new ExitCommand());
        commandMap.put("add", new AddCommand());
        // ... другие команды
    }


    public void execute(String commandName, String[] args) throws IOException {
        ClientCommand command = commandMap.get(commandName.toLowerCase());
        if (command != null) {
            command.clientExecute(args);
        } else {
            System.out.println("Неизвестная команда: " + commandName);
        }
    }

    // Вложенные классы-команды
    private static class HelpCommand implements ClientCommand {
        @Override
        public void clientExecute(String[] args) {
            System.out.println("help, add, exit ...");
        }
    }

    private static class ExitCommand implements ClientCommand {
        @Override
        public void clientExecute(String[] args) {
            System.out.println("Завершение работы клиента...");
            System.exit(0);
        }
    }

    private class AddCommand implements ClientCommand {
        @Override
        public void clientExecute(String[] args) throws IOException {
            Route route = new Route();
            // 2. Заполняем его через inputObject
            route = inputObject.inputObject(route, new KeyboardInputProvider());
            // 3. Сериализуем в JSON (или другой формат)
            String jsonRoute = gson.toJson(route);
            // 4. Формируем запрос с командой и данными
            CommandRequest commandRequest = new CommandRequest("add", jsonRoute);
            String jsonRequest = gson.toJson(commandRequest);
            // 5. Отправляем на сервер
            sendMessage(jsonRequest);
        }
        private void sendMessage(String jsonRequest) throws IOException {
            byte[] data = jsonRequest.getBytes();
            ByteBuffer buf = ByteBuffer.allocate(4 + data.length);
            buf.putInt(data.length);
            buf.put(data);
            buf.flip();

            writeQueue.add(buf);

            SelectionKey key = socketChannel.keyFor(selector);
            if (key != null) {
                key.interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
                selector.wakeup();
            }
        }
    }


}

