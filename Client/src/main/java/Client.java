import Commands.ClientCommand;
import Commands.ClientCommandList;
import Commands.ExecuteScriptClient;
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
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static ToStart.UserSession.currentUsername;

public class Client {




//    public static void main(String[] args) throws IOException {
//        new Client().start("localhost", 5842);
//
//    }



//    public void consoleInputLoop(Scanner scanner) {
//        KeyboardInputProvider provider = new KeyboardInputProvider(scanner);
//        isAuthorized = false;
//        try {
//            System.out.println("Добрый вечер!");
//            System.out.println("Давайте же начнем это увлекательное и, надеюсь, успешное путешествие в мир моей 7й лабораторной");
//            System.out.println("Для начала работы необходимо войти (login) или зарегистрироваться (register)");
//
//            while (!isAuthorized) {
//                System.out.println(" Введите login/register: ");
//                String inputData = scanner.nextLine().trim();
//                if (inputData.isEmpty()) continue;
//
//                String[] parts = inputData.split(" ");
//                String command = parts[0].toLowerCase();
//
//                if (!command.equals("login") && !command.equals("register")) {
//                    System.out.println("Ошибка: сначала необходимо выполнить вход (login) или регистрацию (register).");
//                    continue;
//                }
//                ClientCommandList commandList = ClientCommandList.create(socketChannel, gson, sendMessage, this::checkIdOnServer);
//
//                try {
//                    responseLatch = new CountDownLatch(1);  //пытаюсь засинхронить ответ
//                    processCommand(parts, provider, scanner, commandList, sendMessage);
//                    if (!responseLatch.await(10, TimeUnit.SECONDS)){
//                        System.out.println("Сервер долго молчит нынче...");
//                        return;
//                    }
//                    if (lastResponse != null && lastResponse.isSuccess()) {
//                        isAuthorized = true;
//                        break;
//                    }
//                } catch (IOException e) {
//                    System.err.println("Ошибка при выполнении команды авторизации");
//                }
//            }
//
//            System.out.println("Вы вошли в систему. Теперь доступны все команды.");
//            System.out.println("Если Вы не знаете, какую команду ввести, наберите \"help\" ");
//            System.out.println("Введите команду: ");
//            while (true) {
//
//                String inputData = scanner.nextLine().trim();
//                if (inputData.isEmpty()) continue;
//                String[] parts = inputData.split(" ");
//                String commandName = parts[0];
//                String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
//                if (commandName.equalsIgnoreCase("execute_script")) {
//                    ExecuteScriptClient execScript = new ExecuteScriptClient();
//                    execScript.clientExecute(args.split(" "));
//                    CommandRequest replaceRequest = new CommandRequest("execute_script", args, currentUsername);
//                    sendMessage(gson.toJson(replaceRequest));
//
//                    break;
//                }
//
//
//                ClientCommandList commandList = ClientCommandList.create(socketChannel,gson, sendMessage, this::checkIdOnServer);
//                try {
//                    processCommand(parts, provider, scanner, commandList, sendMessage);
//                } catch (IOException e) {
//                    System.err.println("Ошибка при выполнении команды");
//                    // Можно разблокировать ввод только если команда не была отправлена
//
//                }
//            }
//
//        } catch (NoSuchElementException e) {
//            System.err.println("Ошибка: неожидаемое завершение входного потока. Возможно, была нажата комбинация Ctrl+D или Ctrl+Z.");
//        } catch (IllegalStateException e) {
//            System.err.println("Ошибка: некорректное состояние программы. Попробуйте перезапустить приложение.");
//        }
//        catch (Exception e) {
//            System.err.println("Ошибка при работе программы");
//        }
//
//    }
    // Старая версия ок

}
