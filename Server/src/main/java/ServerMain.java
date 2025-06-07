import Collection.RouteCollectionManager;
import network.TcpServer;

public class ServerMain {

    public static void main(String[] args) {
        try {
            System.out.println("Запуск сервера...");
            TcpServer server = new TcpServer(7878);
            Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
            RouteCollectionManager.init();
            server.start();
        } catch (Exception e) {
            System.err.println("Ошибка сервера");

        }
    }
}
