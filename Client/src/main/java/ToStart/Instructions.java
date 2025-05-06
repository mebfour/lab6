package ToStart;

import java.util.Arrays;
import java.util.Scanner;


public class Instructions {
    public static void greeting(String filePath){
        System.out.println("Добрый вечер!");
        System.out.println("Давайте же начнем это увлекательное и, надеюсь, успешное путешествие в мир моей 5й лабораторной");
        System.out.println("Данные загружены из файла " + filePath);
        System.out.println("Введите команду");
        System.out.println("P.S. Если Вы не знаете, какую команду ввести, наберите \"help\" ");

        while (true){
            Scanner scanner = new Scanner(System.in);
            System.out.println("Введите команду: ");
            String inputComm = scanner.nextLine().trim();
            if (inputComm.isEmpty())continue;

            String[] parts = inputComm.split(" ");
            String commandName = parts[0];
            String[] args = Arrays.copyOfRange(parts, 1, parts.length);
            String argsStr = String.join(" ", args);
            CommandRequest commandRequest = new CommandRequest(commandName, argsStr);
        }
    }
}