package InputHandler;

import Commands.CommandRequest;
import Commands.CommandResponse;
import com.google.gson.Gson;
import managers.CommandManager;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;

public class ClientHandler implements Runnable {
    private final SocketChannel socketChannel;
    private final Gson gson = new Gson();

    // Буферы для чтения
    private final ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
    private ByteBuffer dataBuffer = null;

    // Очередь буферов для записи
    private final Queue<ByteBuffer> writeQueue = new LinkedList<>();

    public ClientHandler(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;

    }

    @Override
    public void run() {
        try {
            socketChannel.configureBlocking(false);

            while (socketChannel.isOpen()) {
                // Читаем сообщения
                if (!readMessage()) {
                    // Соединение закрыто клиентом
                    break;
                }

                // Пишем ответы из очереди
                writeMessages();
            }
        } catch (IOException e) {
            System.err.println("Ошибка в обработчике клиента: " + e.getMessage());
        } finally {
            try {
                SocketAddress remote = socketChannel.getRemoteAddress();
                socketChannel.close();
                System.out.println("Соединение с клиентом " + remote + " закрыто");
            } catch (IOException ignored) {}
        }
    }

    private boolean readMessage() throws IOException {
        // Сначала читаем длину сообщения
        if (dataBuffer == null) {
            int read = socketChannel.read(lengthBuffer);
            if (read == -1) {
                return false; // клиент закрыл соединение
            }
            if (lengthBuffer.remaining() > 0) {
                return true; // длина еще не полностью прочитана, ждем следующего вызова
            }
            lengthBuffer.flip();
            int length = lengthBuffer.getInt();
            lengthBuffer.clear();

            if (length <= 0) {
                System.err.println("Некорректная длина сообщения: " + length);
                return false;
            }
            dataBuffer = ByteBuffer.allocate(length);
        }

        // Читаем тело сообщения
        int read = socketChannel.read(dataBuffer);
        if (read == -1) {
            return false; // клиент закрыл соединение
        }
        if (dataBuffer.remaining() > 0) {
            return true; // ждем следующий вызов для полного чтения
        }

        // Полностью прочитали сообщение
        dataBuffer.flip();
        byte[] data = new byte[dataBuffer.limit()];
        dataBuffer.get(data);
        dataBuffer = null; // готовимся к следующему сообщению

        String jsonRequest = new String(data);
        System.out.println("Получена строка: " + jsonRequest);

        CommandRequest request;
        try {
            request = gson.fromJson(jsonRequest, CommandRequest.class);
            System.out.println("Обрабатываем команду: " + request.getCommandName());
        } catch (Exception e) {
            // Некорректный JSON
            sendErrorResponse("Некорректный формат команды");
            return true; // продолжаем работу
        }

        if (request.getCommandName().equalsIgnoreCase("exit")) {
            sendResponse("Сеанс завершён.");
            return false; // завершаем работу с клиентом
        }
        new Thread(() -> {
            CommandResponse response = CommandManager.checkComm(request);
            String jsonResponse = gson.toJson(response);
            sendResponse(jsonResponse);
        }).start();

        return true;
    }

    private void sendErrorResponse(String message) {
        CommandResponse errorResponse = new CommandResponse(message, false);
        String jsonResponse = gson.toJson(errorResponse);
        sendResponse(jsonResponse);
    }

    private void sendResponse(String jsonResponse) {
        byte[] data = jsonResponse.getBytes();
        ByteBuffer buf = ByteBuffer.allocate(4 + data.length);
        buf.putInt(data.length);
        buf.put(data);
        buf.flip();

        synchronized (writeQueue) {
            writeQueue.add(buf);
        }
    }

    private void writeMessages() throws IOException {
        synchronized (writeQueue) {
            while (!writeQueue.isEmpty()) {
                ByteBuffer buf = writeQueue.peek();
                socketChannel.write(buf);
                if (buf.hasRemaining()) {
                    // Не все данные отправлены, ждем следующего вызова
                    break;
                }
                writeQueue.poll(); // сообщение отправлено полностью
            }
        }
    }
}
