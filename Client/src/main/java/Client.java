import java.io.*;
import java.net.Socket;

public class Client {

    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    // Метод для подключения к серверу
    public void connect(String serverAddress, int port) {
        try {
            System.out.println("Подключение к серверу...");
            socket = new Socket(serverAddress, port); // Создаем сокет для подключения к серверу
            System.out.println("Подключено к серверу!");

            // Инициализируем потоки ввода-вывода
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Ошибка подключения: " + e.getMessage());
        }
    }

    // Метод для отправки команды "exit"
    public void sendExitCommand() {
        try {
            if (outputStream == null || inputStream == null) {
                System.err.println("Соединение не установлено.");
                return;
            }

            // Отправляем команду "exit"
            outputStream.writeUTF("exit");
            outputStream.flush();
            System.out.println("Команда 'exit' отправлена на сервер.");

            // Получаем ответ от сервера
            String response = inputStream.readUTF();
            System.out.println("Ответ от сервера: " + response);
        } catch (IOException e) {
            System.err.println("Ошибка соединения: " + e.getMessage());
        }
    }

    // Метод для закрытия соединения
    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Соединение закрыто.");
            }
        } catch (IOException e) {
            System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
        }
    }

    // Точка входа в программу
    public static void main(String[] args) {
        // Создаем экземпляр клиента
        Client client = new Client();

        // Подключаемся к серверу (укажите адрес и порт сервера)
        client.connect("localhost", 8080);

        // Отправляем команду "exit"
        client.sendExitCommand();

        // Закрываем соединение
        client.disconnect();
    }
}