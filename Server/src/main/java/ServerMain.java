import Collection.RouteCollectionManager;
import Commands.CommandProcessor;
import network.TcpServer;

public class ServerMain {

    public static void main(String[] args) {
        try {
            System.out.println("Запуск сервера...");
            RouteCollectionManager manager = new RouteCollectionManager();
            CommandProcessor processor = new CommandProcessor(manager);
            TcpServer server = new TcpServer(processor, 7878);
            Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
            RouteCollectionManager.init("D:/itmo/jaba/lab6/Server/src/main/java/file.xml");
            server.start();

        } catch (Exception e) {
            System.err.println("Ошибка сервера");
        }
    }
}
