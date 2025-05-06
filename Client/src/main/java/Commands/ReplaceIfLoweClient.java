package Commands;


import java.util.Scanner;

public class ReplaceIfLoweClient implements ClientCommand<String[]> {
    @Override
    public String[] clientExecute(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String targetKey;
        String newKey;

        // Получаем ключ элемента, который хотим заменить
        if (args.length >= 1 && args[0] != null && !args[0].trim().isEmpty()) {
            targetKey = args[0].trim();
        } else {
            while (true) {
                System.out.print("Введите ключ элемента, который хотите заменить: ");
                targetKey = scanner.nextLine().trim();
                if (!targetKey.isEmpty()) break;
                System.out.println("Ключ не может быть пустым. Повторите ввод.");
            }
        }

        // Получаем новый ключ
        if (args.length >= 2 && args[1] != null && !args[1].trim().isEmpty()) {
            newKey = args[1].trim();
        } else {
            while (true) {
                System.out.print("Введите новый ключ: ");
                newKey = scanner.nextLine().trim();
                if (!newKey.isEmpty()) break;
                System.out.println("Ключ не может быть пустым. Повторите ввод.");
            }
        }

        return new String[]{targetKey, newKey};
    }
}

