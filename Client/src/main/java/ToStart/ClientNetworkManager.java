package ToStart;

import Commands.ClientCommand;
import Commands.ClientCommandList;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Alert;
import javafx.util.Pair;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import static ToStart.PasswordUtil.hashPassword;
import static ToStart.UserSession.currentUsername;

public class ClientNetworkManager {
    private volatile CommandResponse lastResponse = null;
    private SocketChannel socketChannel;    //  Каждый SocketChannel, зарегистрированный в Selector, имеет связанный объект SelectionKey
    private Selector selector;      //  позволяет одному потоку ожидать событий на множестве открытых каналов.
    private final ByteBuffer readLengthBuffer = ByteBuffer.allocate(4); // для чтения длины
    private ByteBuffer readDataBuffer = null; // для чтения данных сообщения
    private final Queue<ByteBuffer> writeQueue = new ConcurrentLinkedQueue<>();
    private final Consumer<String> sendMessage;
    private CountDownLatch responseLatch;
    public static CommandResponse routeResponse;
    private final SimpleObjectProperty<CommandResponse> commandResponse = new SimpleObjectProperty<>();
    private final Gson gson = new Gson();

    public ObjectProperty<CommandResponse> commandResponseProperty() {
        return commandResponse;
    }
    private final ObjectProperty<CommandResponse> routeResponseProperty = new SimpleObjectProperty<>();

    public ObjectProperty<CommandResponse> routeResponseProperty() {
        return routeResponseProperty;
    }

    public Gson getGson() {
        return gson;
    }



    public Pair<Boolean, String> authenticate(String username, String password)  {
        Map<String, String> params = new HashMap<>();
        try {
            password = hashPassword(password);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        params.put("username", username);
        params.put("password", password);
        String jsonParams = gson.toJson(params);

        CommandRequest authRequest = new CommandRequest("login", jsonParams, username);
        String jsonRequest = gson.toJson(authRequest);

        responseLatch = new CountDownLatch(1);
        sendMessage.accept(jsonRequest);
        boolean awaited = false;
        try {
            awaited = responseLatch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!awaited) {
            System.out.println("Сервер долго молчит...");
            return new Pair<>(false, "Ошибка входа");
        }

        if (responseLatch != null) {
            responseLatch.countDown();
        }
        System.out.println("После первого запроса на регистрацию");
        System.out.println("Успех - " + lastResponse.isSuccess());
        System.out.println("Сообщение - " + lastResponse.getMessage());
        return new Pair<>(lastResponse.isSuccess(), lastResponse.getMessage());
    }
    public ClientNetworkManager() {
        this.sendMessage = json -> {
            try {
                sendMessage(json);
            } catch (IOException e) {
                System.err.println("Ошибка при отправке сообщения: " + e.getMessage());
            }
        };
    }

    public void sendMessage(String  jsonRequest) throws IOException {
        byte[] data = jsonRequest.getBytes();   //преобразует в массив байт
        ByteBuffer buf = ByteBuffer.allocate(4 + data.length);
        buf.putInt(data.length); // записываем длину данных
        buf.put(data);           // записываем сами данные
        buf.flip();              // готовим буфер к чтению

        writeQueue.add(buf);      // добавляем буфер в очередь на отправку

        SelectionKey key = socketChannel.keyFor(selector);
        if (key != null) {
            key.interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ); // говорим, что хотим писать и читать
            selector.wakeup(); // "будим" селектор, чтобы он обработал интересующие нас события
        }
    }

    public boolean checkIdOnServer(int id) {
        try {
            CommandRequest checkIdRequest = new CommandRequest("check_id", String.valueOf(id), currentUsername);
            String jsonRequest = gson.toJson(checkIdRequest);
            // Отправляем запрос
            responseLatch = new CountDownLatch(1);
            sendMessage.accept(jsonRequest); // отправляем запрос
            responseLatch.await(); // ждём ответа
            return lastResponse != null && lastResponse.isSuccess();
        } catch (Exception e) {
            System.err.println("Ошибка при проверке id на сервере");
            return false;
        }
    }
    public void processCommand(String[] inp,  ClientCommandList clientCommandList, Consumer<String> sendMessage) throws IOException  {
        String args = String.join(" ", Arrays.asList(inp).subList(1, inp.length));
        for (ClientCommand command : clientCommandList) {
            if (command.getName().equals(inp[0])) {
                try {
                    command.clientExecute(inp, args);
                    return;
                } catch (IOException | NoSuchAlgorithmException e) {
                    System.err.println("Ошибка при выполнении команды " + inp[0]);
                    return;
                }
            }
        }
        CommandRequest defaultRequest = new CommandRequest(inp[0], args, currentUsername);
        sendMessage.accept(gson.toJson(defaultRequest));
    }

    private void finishConnection(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        if (sc.isConnectionPending()) {
            sc.finishConnect();
        }
        System.out.println("Подключено к серверу");
        sc.register(selector, SelectionKey.OP_READ);
    }

    // Старая версия ок
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
            System.out.println("ТЕКУЩИЙ ОТВЕТ СЕРВЕРА: " + response.getMessage());
            if (response.getMessage().trim().startsWith("{")){
                routeResponse = response;
                this.routeResponseProperty.set(response);
                this.commandResponse.set(response);
            }

            readDataBuffer = null; // готовимся к следующему сообщению

            this.lastResponse = response;

            if (responseLatch != null) {
                responseLatch.countDown();
            }
        }

    }
    // Старая версия ок
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
    // Старая версия ок
    private void closeConnection(SocketChannel sc, SelectionKey key) throws IOException {
        System.out.println("Сервер закрыл соединение");
        key.cancel();
        sc.close();
        System.exit(0);
    }

    public void loadRoutesFromMapAsync() {
        String[] parts = "get_routes".split(" ");
        try {
            ClientCommandList commandList = ClientCommandList.create(socketChannel, gson, sendMessage, this::checkIdOnServer);
            processCommand(parts, commandList, sendMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendCommand(String commandName, String key, String username) {
        CommandRequest request = new CommandRequest(commandName, key, username);
        String json = gson.toJson(request);
        try {
            sendMessage(json);
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка сети");
                alert.setHeaderText(null);
                alert.setContentText("Не удалось отправить команду на сервер.");
                alert.showAndWait();
            });
        }

    }
    public void sendGetRoutesCommand() {
        CommandRequest request = new CommandRequest("get_routes", "", currentUsername);
        String json = gson.toJson(request);

        try {
            sendMessage(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(String host, int port) throws IOException {
        try {
            /**
             * Создаем селектор — механизм, который позволяет приложению одновременно отслеживать несколько каналов
             * (например, сетевых подключений) и реагировать только на те из них, которые готовы к выполнению какого-либо
             * действия (например, подключение установлено, данные доступны для чтения, можно писать данные и т.д.).
             */
            selector = Selector.open(); //После этого можно регистрировать каналы в селекторе
            /**
             * Создаётся сокетный канал (SocketChannel), который представляет клиентский TCP-сокет (аналог Socket из
             * классического java.net, но в NIO).
             */
            socketChannel = SocketChannel.open();
            /**
             * Переключает канал в неблокирующий режим. Это значит, что операции чтения или записи не будут ждать
             * завершения, если они не могут быть выполнены сразу.
             * Например, если вы попытаетесь прочитать данные, когда их ещё нет — метод вернёт 0, а не будет ждать.
             */
            socketChannel.configureBlocking(false);
            /**
             * Пытается установить TCP-соединение с сервером по указанному адресу (host:port). Однако, поскольку канал
             * в неблокирующем режиме, эта операция может не завершиться сразу — она начнётся асинхронно.
             * То есть, возможно, соединение ещё не установлено, но мы уже регистрируем интерес к событию завершения.
             */
            socketChannel.connect(new InetSocketAddress(host, port));
            /**
             * Регистрирует этот канал в ранее созданном селекторе, указывая, что нас интересует событие OP_CONNECT ,
             * то есть момент, когда соединение с сервером будет установлено.
             * Когда соединение действительно установится, ключ (SelectionKey) этого канала станет "готовым", и вы сможете
             * обработать событие в цикле selector.select().
             */
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            // Создаем и запускаем поток для пользовательского ввода

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
            e.printStackTrace();
            System.err.println("Ошибка подключения");
        }

    }

    public Consumer<String> getSendMessage() {
        return sendMessage;
    }


}
