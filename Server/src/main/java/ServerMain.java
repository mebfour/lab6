import Collection.RouteCollectionManager;
import Commands.CommandExecutor;
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
            server.start();

        } catch (Exception e) {
            System.err.println("Ошибка сервера");
        }
    }
}
