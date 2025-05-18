import Commands.ClientCommand;
import Commands.ClientCommandList;
import Commands.IdChecker;
import InputHandler.InputProvider;
import InputHandler.KeyboardInputProvider;
import ToStart.CommandRequest;
import ToStart.CommandResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class IdoIt {
    private volatile CommandResponse lastResponse = null;
    private volatile boolean readyForInput = true;
    private SocketChannel socketChannel;
    private Selector selector;
    private  final Gson gson = new Gson();
    private final Scanner scanner = new Scanner(System.in);
    private final ByteBuffer readLengthBuffer = ByteBuffer.allocate(4); // для чтения длины
    private ByteBuffer readDataBuffer = null; // для чтения данных сообщения
    private final Queue<ByteBuffer> writeQueue = new ConcurrentLinkedQueue<>();
    IdChecker idChecker = this::checkIdOnServer;
    private final Consumer<String> sendMessage;


    public IdoIt() {
        this.sendMessage = json -> {
            try {
                sendMessage(json);
            } catch (IOException e) {
                System.err.println("Ошибка при отправке сообщения: " + e.getMessage());
            }
        };
    }

    public static void main(String[] args) throws IOException {
        new IdoIt().start("localhost", 7878);

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

    // Готово и не проверено
    public boolean checkIdOnServer(int id) {
        try {
            CommandRequest checkIdRequest = new CommandRequest("check_id", String.valueOf(id));
            String jsonRequest = gson.toJson(checkIdRequest);

            // Отправляем запрос
            sendMessage.accept(jsonRequest);

            CommandResponse[] responseHolder = new CommandResponse[1];


            readyForInput = false;
            while (!readyForInput) {
                Thread.sleep(10);
            }


            return lastResponse != null && lastResponse.isSuccess();

        } catch (Exception e) {
            System.err.println("Ошибка при проверке id на сервере");
            return false;
        }
    }



    // Вроде готово и не проверено
    public void processCommand(String[] inp, InputProvider provider, Scanner scanner, ClientCommandList clientCommandList, Consumer<String> sendMessage) throws IOException  {
        String args = String.join(" ", Arrays.asList(inp).subList(1, inp.length));
        for (ClientCommand command : clientCommandList) {
            if (command.getName().equals(inp[0])) {
                try {
                    command.clientExecute(inp, args, provider, scanner);
                    return;
                } catch (IOException e) {
                    System.err.println("Ошибка при выполнении команды " + inp[0]);
                    return;
                }
            }
        }
        CommandRequest defaultRequest = new CommandRequest(inp[0], args);
        sendMessage.accept(gson.toJson(defaultRequest));
    }



    // Готово и не проверено
    public void consoleInputLoop(String filePath, Scanner scanner) {
        KeyboardInputProvider provider = new KeyboardInputProvider(scanner);
        try {
            System.out.println("Добрый вечер!");
            System.out.println("Давайте же начнем это увлекательное и, надеюсь, успешное путешествие в мир моей 5й лабораторной");
            System.out.println("Данные загружены из файла " + filePath);
            System.out.println("P.S. Если Вы не знаете, какую команду ввести, наберите \"help\" ");

            while (true) {
                if (readyForInput) {
                    System.out.print("Введите команду: ");
                    String inputData = scanner.nextLine().trim();

                    if (inputData.isEmpty()) continue;
                    String[] parts = inputData.split(" ");
                    String commandName = parts[0];
                    /**
                     * Arrays.asList(parts) — превращает массив в список.
                     * .subList(1, parts.length) — берёт подсписок, начиная с индекса 1 до конца.
                     * String.join(" ", ...) — соединяет элементы списка через пробел.
                     */
                    String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));

                    readyForInput = false;



                    ClientCommandList commandList = ClientCommandList.create(socketChannel,gson, sendMessage, idChecker);
                    try {
                        processCommand(parts, provider, scanner, commandList, sendMessage);
                    } catch (IOException e) {
                        System.err.println("Ошибка при выполнении команды");
                        // Можно разблокировать ввод только если команда не была отправлена
                        readyForInput = true;
                    }
                }
            }
        } catch (NoSuchElementException e) {
            System.err.println("Ошибка: неожидаемое завершение входного потока. Возможно, была нажата комбинация Ctrl+D или Ctrl+Z.");
        } catch (IllegalStateException e) {
            System.err.println("Ошибка: некорректное состояние программы. Попробуйте перезапустить приложение.");
        }
        catch (Exception e) {
            System.err.println("Ошибка!");
        }

    }
    // Старая версия ок
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

            System.out.println(response.getMessage());
            readDataBuffer = null; // готовимся к следующему сообщению

            this.lastResponse = response;
            readyForInput = true;
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
                    consoleInputLoop("default_file_path", scanner);
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
}
