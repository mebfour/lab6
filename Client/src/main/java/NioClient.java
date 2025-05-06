import Classes.Route;
import Commands.ClientCommand;
import InputHandler.KeyboardInputProvider;
import InputHandler.inputObject;
import ToStart.CommandRequest;
import ToStart.CommandResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NioClient {


    private SocketChannel socketChannel;
    private Selector selector;
    private final Gson gson = new Gson();
    private final Map<String, ClientCommand> commandMap = new HashMap<>();

    private final ByteBuffer readLengthBuffer = ByteBuffer.allocate(4); // для чтения длины
    private ByteBuffer readDataBuffer = null; // для чтения данных сообщения

    private final Queue<ByteBuffer> writeQueue = new ConcurrentLinkedQueue<>();

    public void start(String host, int port) throws IOException {
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(host, port));
            socketChannel.register(selector, SelectionKey.OP_CONNECT);

            new Thread(() -> consoleInputLoop("file.xml")).start();


            while (true) {
                selector.select();

                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();

                    if (!key.isValid()) continue;

                    if (key.isConnectable()) {
                        finishConnection(key);
                    }
                    if (key.isReadable()) {
                        readFromServer(key);
                    }
                    if (key.isWritable()) {
                        writeToServer(key);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка подключения");
        }
    }

    private void finishConnection(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        if (sc.isConnectionPending()) {
            sc.finishConnect();
        }
        System.out.println("Подключено к серверу");
        sc.register(selector, SelectionKey.OP_READ);
    }

    private void readFromServer(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();

        // Сначала читаем длину сообщения (4 байта)
        if (readDataBuffer == null) {
            int read = sc.read(readLengthBuffer);
            if (read == -1) {
                closeConnection(sc, key);
                return;
            }
            if (readLengthBuffer.remaining() == 0) {
                readLengthBuffer.flip();
                int length = readLengthBuffer.getInt();
                readDataBuffer = ByteBuffer.allocate(length);
                readLengthBuffer.clear();
            } else {
                return; // ждем пока полностью прочитаем длину
            }
        }

        // Читаем само сообщение
        int read = sc.read(readDataBuffer);
        if (read == -1) {
            closeConnection(sc, key);
            return;
        }
        if (readDataBuffer.remaining() == 0) {
            readDataBuffer.flip();
            byte[] data = new byte[readDataBuffer.limit()];
            readDataBuffer.get(data);
            String jsonResponse = new String(data);

            CommandResponse response = gson.fromJson(jsonResponse, CommandResponse.class);
            if (!response.isSuccess()) {
                System.out.println("Ошибка: " + response.getMessage());
                // Просто продолжаем цикл, приглашая пользователя ввести команду заново
            }
            System.out.println("Ответ сервера:\n" + response.getMessage());
            System.out.print("Введите команду: ");

            readDataBuffer = null; // готовимся к следующему сообщению
        }
    }

    private void writeToServer(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();

        while (!writeQueue.isEmpty()) {
            ByteBuffer buf = writeQueue.peek();
            sc.write(buf);
            if (buf.hasRemaining()) {
                // Канал не смог записать всё, ждем следующего вызова write
                break;
            }
            writeQueue.poll(); // сообщение полностью отправлено, удаляем из очереди
        }

        if (writeQueue.isEmpty()) {
            // Нет данных для записи, переключаемся на чтение
            sc.register(selector, SelectionKey.OP_READ);
        }
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

    private void consoleInputLoop(String filePath) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("Добрый вечер!");
            System.out.println("Давайте же начнем это увлекательное и, надеюсь, успешное путешествие в мир моей 5й лабораторной");
            System.out.println("Данные загружены из файла " + filePath);
            System.out.println("Введите команду");
            System.out.println("P.S. Если Вы не знаете, какую команду ввести, наберите \"help\" ");

            while (true) {
                System.out.print("Введите команду: ");
                String inputComm = scanner.nextLine().trim();

                if (inputComm.isEmpty()) continue;

                String[] parts = inputComm.split(" ");
                String commandName = parts[0];
                String[] args = Arrays.copyOfRange(parts, 1, parts.length);
                String argsStr = String.join(" ", args);


                switch (commandName) {
                    case "exit":
                        System.out.println("Завершение работы клиента...");
                        socketChannel.close();
                        System.exit(0);
                        break;
                    case "add":
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
                        break;
                    default:
                        // Остальные команды
                        CommandRequest defaultRequest = new CommandRequest(commandName, argsStr);
                        String defaultJsonRequest = gson.toJson(defaultRequest);
                        sendMessage(defaultJsonRequest);
                        break;

                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка");
        }
    }

    private void closeConnection(SocketChannel sc, SelectionKey key) throws IOException {
        System.out.println("Сервер закрыл соединение");
        key.cancel();
        sc.close();
        System.exit(0);
    }

    public static void main(String[] args) throws IOException {
        new NioClient().start("localhost", 7878);
    }
}
