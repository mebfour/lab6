import ToStart.CommandRequest;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

import ToStart.CommandResponse;
import com.google.gson.Gson;

public class Client {

    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private Gson gson = new Gson();

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
    public void startInteractiveSession(String filePath) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("Введите команду: ");
                String inputComm = scanner.nextLine().trim();

                if (inputComm.isEmpty()) {
                    continue; // игнорируем пустые строки
                }
                String[] parts = inputComm.split(" ");
                String commandName = parts[0];
                String[] args = Arrays.copyOfRange(parts, 1, parts.length);
                String argsStr = String.join(" ", args);
                CommandRequest commandRequest = new CommandRequest(commandName, argsStr);

                // Сериализуем в JSON
                String jsonRequest = gson.toJson(commandRequest);
                // Отправляем команду на сервер
                outputStream.writeUTF(jsonRequest);
                outputStream.flush();

                // Читаем ответ от сервера
                String jsonResponse = inputStream.readUTF();
                CommandResponse response = gson.fromJson(jsonResponse, CommandResponse.class);

                System.out.println("Сообщение от сервера:\n" + response.getMessage());

                // Если команда exit, то завершаем цикл
                if (inputComm.equalsIgnoreCase("exit")) {
                    System.out.println("Завершение сессии.");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка во время сессии"
            );
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.connect("localhost", 7878);

        if (client.outputStream != null && client.inputStream != null) {
            client.startInteractiveSession("file.xml");
        }
    }
}
