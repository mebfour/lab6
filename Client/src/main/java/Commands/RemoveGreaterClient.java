package Commands;

import java.io.IOException;
import java.util.Scanner;

public class RemoveGreaterClient implements ClientCommand<String> {
    @Override
    public String clientExecute(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String inpKey;

        if (args.length >= 1 && args[0] != null && !args[0].trim().isEmpty()) {
            inpKey = args[0].trim();
        } else {
            while (true) {
                System.out.print("Введите ключ элемента: ");
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

