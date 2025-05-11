package network;

import InputHandler.ClientHandler;
import Commands.CommandProcessor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServer {
    private final CommandProcessor processor;
    private final int port;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ExecutorService clientHandlersPool;

    public TcpServer(CommandProcessor processor, int port) {
        this.processor = processor;
        this.port = port;
    }

    public void start() throws IOException {
        // Открываем серверный канал и селектор
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);

        selector = Selector.open();

        // Регистрируем серверный канал на OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // Пул потоков для обработки клиентов
        clientHandlersPool = Executors.newCachedThreadPool();

        System.out.println("Сервер запущен на порту " + port);

        while (true) {
            selector.select(); // ждем событий

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();

                if (!key.isValid()) continue;

                if (key.isAcceptable()) {
                    acceptClient(key);
                }
            }
        }
    }

    private void acceptClient(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        if (clientChannel != null) {
            clientChannel.configureBlocking(true); // Для ClientHandler используем блокирующий режим
            System.out.println("Новое подключение от " + clientChannel.getRemoteAddress());

            // Запускаем ClientHandler в отдельном потоке
            ClientHandler handler = new ClientHandler(clientChannel, processor);
            clientHandlersPool.submit(handler);
        }
    }

    public void stop() {
        try {
            if (selector != null) selector.close();
            if (serverSocketChannel != null) serverSocketChannel.close();
            if (clientHandlersPool != null) clientHandlersPool.shutdownNow();
            System.out.println("Сервер остановлен.");
        } catch (IOException e) {
            System.err.println("Ошибка при остановке сервера: " + e.getMessage());
        }
    }
}
