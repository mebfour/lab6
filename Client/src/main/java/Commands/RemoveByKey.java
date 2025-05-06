package Commands;

import Classes.Route;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class RemoveByKey implements ClientCommand<String> {


    @Override
    public String clientExecute(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String inpKey = null;

        // Если аргумент передан сразу после команды
        if (args.length >= 1 && args[0] != null && !args[0].trim().isEmpty()) {
            inpKey = args[0].trim();
        } else {
            // Запрашиваем ввод с консоли
            while (true) {
                System.out.print("Введите ключ элемента, который Вы хотите удалить: ");
                inpKey = scanner.nextLine().trim();
                if (!inpKey.isEmpty()) {
                    break;
                }
                System.out.println("Ключ не может быть пустым. Повторите ввод.");
            }
        }
        return inpKey;
    }

}
