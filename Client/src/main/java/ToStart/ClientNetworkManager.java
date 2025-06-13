package ToStart;

import Classes.Route;
import Classes.RouteDTO;
import Commands.ClientCommand;
import Commands.ClientCommandList;
import InputHandler.InputProvider;
import InputHandler.JsonToRouteMapper;
import InputHandler.KeyboardInputProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.lang.reflect.Type;
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
import java.util.stream.Collectors;

import static ToStart.PasswordUtil.hashPassword;
import static ToStart.UserSession.currentUsername;

public class ClientNetworkManager {
    private volatile CommandResponse lastResponse = null;
    private SocketChannel socketChannel;    //  Каждый SocketChannel, зарегистрированный в Selector, имеет связанный объект SelectionKey
    private Selector selector;      //  позволяет одному потоку ожидать событий на множестве открытых каналов.
    private  final Gson gson = new Gson();
    private final Scanner scanner = new Scanner(System.in);
    private final ByteBuffer readLengthBuffer = ByteBuffer.allocate(4); // для чтения длины
    private ByteBuffer readDataBuffer = null; // для чтения данных сообщения
    private final Queue<ByteBuffer> writeQueue = new ConcurrentLinkedQueue<>();
    private final Consumer<String> sendMessage;
    private boolean isAuthorized;
    private CountDownLatch responseLatch;
    public static CommandResponse routeResponse;
    private final SimpleObjectProperty<CommandResponse> commandResponse = new SimpleObjectProperty<>();

    public ObjectProperty<CommandResponse> commandResponseProperty() {
        return commandResponse;
    }

    private final ObjectProperty<CommandResponse> routeResponseProperty = new SimpleObjectProperty<>();

    public ObjectProperty<CommandResponse> routeResponseProperty() {
        return routeResponseProperty;
    }

    public void setRouteResponse(CommandResponse response) {
        routeResponseProperty.set(response);
    }




    public boolean authenticate(String username, String password) {

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
            return false;
        }

        if (responseLatch != null) {
            responseLatch.countDown();
        }
        System.out.println(lastResponse.isSuccess());

        return  lastResponse.isSuccess();
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
    public void processCommand(String[] inp, InputProvider provider, Scanner scanner, ClientCommandList clientCommandList, Consumer<String> sendMessage) throws IOException  {
        String args = String.join(" ", Arrays.asList(inp).subList(1, inp.length));
        for (ClientCommand command : clientCommandList) {
            if (command.getName().equals(inp[0])) {
                try {
                    command.clientExecute(inp, args, provider, scanner);
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
            System.out.println("ТЕКУЩИЙ ОТВЕТ СЕРВЕРА" + response.getMessage());
            if (response.getMessage().trim().startsWith("{")){
                routeResponse = response;
                this.routeResponseProperty.set(response);
                this.commandResponse.set(response);
            }

            if (isAuthorized && (!response.getMessage().equals("Id найден"))) {
                System.out.print("Введите команду: ");
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
        KeyboardInputProvider provider = new KeyboardInputProvider(scanner);
        String[] parts = "get_routes".split(" ");
        try {
            ClientCommandList commandList = ClientCommandList.create(socketChannel, gson, sendMessage, this::checkIdOnServer);
            processCommand(parts, provider, scanner, commandList, sendMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ObservableList<RouteDTO> loadRoutesFromMap() {
        // Преобразуем Map в ObservableList
        if (lastResponse != null) {
            String json = lastResponse.getMessage();

            if (!lastResponse.isSuccess()) {
                System.out.println("Ошибка от сервера: " + lastResponse.getMessage());
                return FXCollections.emptyObservableList();
            }
            if (json == null || json.trim().isEmpty()) {
                System.out.println("Получена пустая коллекция");
                return FXCollections.emptyObservableList();
            }

            Map<String, RouteDTO> routeMap = JsonToRouteMapper.parseJsonToRouteMap(json);
            ObservableList<RouteDTO> list = FXCollections.observableArrayList(routeMap.values());
            return list;
//            try {
//                Type mapType = new TypeToken<Map<String, RouteDTO>>() {
//                }.getType();
//                Map<String, RouteDTO> routeMap = new Gson().fromJson(json, mapType);
//
//                ObservableList<RouteDTO> routes = FXCollections.observableArrayList();
//                if (routeMap != null) {
//                    routes.addAll(routeMap.values());
//                }
//                return routes;
//            } catch (Exception e) {
//                System.err.println("Ошибка десериализации JSON: " + e.getMessage());
//                return FXCollections.emptyObservableList();
//            }
//        }
//        return FXCollections.emptyObservableList();

        }
        System.out.println("Возвращаем пустую мапу");
        return FXCollections.emptyObservableList();
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
            Thread inputThread = new Thread(() -> {
                try {
                    authorize(scanner);
                } catch (Exception e) {
                    System.err.println("Ошибка в цикле ввода");
                }
            });
            inputThread.start();

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

    public boolean authorize(Scanner scanner) {
        KeyboardInputProvider provider = new KeyboardInputProvider(scanner);
        isAuthorized = false;
        try {
            System.out.println("Добрый вечер!");
            System.out.println("Давайте же начнем это увлекательное и, надеюсь, успешное путешествие в мир моей 7й лабораторной");
            System.out.println("Для начала работы необходимо войти (login) или зарегистрироваться (register)");

            while (!isAuthorized) {
                System.out.println(" Введите login/register: ");
                String inputData = scanner.nextLine().trim();
                if (inputData.isEmpty()) continue;

                String[] parts = inputData.split(" ");
                String command = parts[0].toLowerCase();

                if (!command.equals("login") && !command.equals("register")) {
                    System.out.println("Ошибка: сначала необходимо выполнить вход (login) или регистрацию (register).");
                    continue;
                }

                ClientCommandList commandList = ClientCommandList.create(socketChannel, gson, sendMessage, this::checkIdOnServer);

                try {
                    responseLatch = new CountDownLatch(1);  //пытаюсь засинхронить ответ
                    processCommand(parts, provider, scanner, commandList, sendMessage);
                    if (!responseLatch.await(10, TimeUnit.SECONDS)){
                        System.out.println("Сервер долго молчит нынче...");
                        return false;
                    }
                    if (lastResponse != null && lastResponse.isSuccess()) {
                        isAuthorized = true;
                        break;
                    }
                } catch (IOException | InterruptedException e) {
                    System.err.println("Ошибка при выполнении команды авторизации");
                }
            }
            return isAuthorized;
        } catch (Exception e) {
            System.err.println("Ошибка при работе программы");
            return false;
        }
    }

    public Consumer<String> getSendMessage() {
        return sendMessage;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }


    private ObservableList<RouteDTO> run() {
        int attempts = 0;
        final int maxAttempts = 30; // максимум 30 попыток
        final long delayMillis = 500; // задержка между попытками

        while (attempts < maxAttempts) {
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            if (lastResponse != null) {
                Map<String, RouteDTO> routeList = lastResponse.getRouteList();

                if (routeList != null && !routeList.isEmpty()) {
                    ObservableList<RouteDTO> routes = FXCollections.observableArrayList(routeList.values());
                    return routes;
                }
            }

            attempts++;
        }
        // После всех попыток, если данные так и не появились
        Platform.runLater(() -> System.out.println("Данные не загружены: lastResponse или routeList остались null"));
        return null;
    }


        public ObservableList<RouteDTO> loadRoutesWithRetry(int maxAttempts, long delayMillis) {
            int attempt = 0;

            while (attempt++ < maxAttempts) {
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Загрузка прервана");
                    return FXCollections.emptyObservableList();
                }

                if (lastResponse != null) {
                    Map<String, RouteDTO> routeList = lastResponse.getRouteList();

                    if (routeList != null && !routeList.isEmpty()) {
                        ObservableList<RouteDTO> routes = FXCollections.observableArrayList(routeList.values());
                        return routes; // Данные загружены
                    }
                }
            }


            System.out.println("Данные не загружены: lastResponse или routeList остались null");
            return FXCollections.emptyObservableList();
        }




}
