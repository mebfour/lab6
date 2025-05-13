package Commands;

import Classes.Route;
import InputHandler.InputProvider;
import InputHandler.inputObject;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Scanner;

public class UpdateIdClient {

    public String clientExecute(String[] args, InputProvider provider) throws IOException {
        Scanner scanner = new Scanner(System.in);
        int id;
        if (args.length > 0) {
            id = Integer.parseInt(args[0]);
        } else {
            while (true) {
                System.out.print("Введите id: ");
                String line = scanner.nextLine().trim();

                if (line.isEmpty()) {
                    System.out.println("id не может быть пустым. Повторите ввод.");
                    continue;
                }
                try {
                    id = Integer.parseInt(line);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("id должен быть целым числом. Повторите ввод.");
                    continue;
                }

            }
        }

        // Получаем новые данные для объекта
        Route updatedRoute = inputObject.inputObject(new Route(), provider);
        updatedRoute.setId(id);

        Gson gson = new Gson();
        return gson.toJson(updatedRoute);
    }
}
