package Commands.BaseCom;

import Commands.Command;
import Commands.CommandResponse;

import java.util.Date;

import static managers.CommandManager.collectionManager;

public class InfoCommand implements Command {

    @Override
    public CommandResponse execute(String args) {

        // Получаем данные о коллекции
        Date time = collectionManager.getInitializationTime();
        String type = collectionManager.getCollectionType().getSimpleName();
        int size = collectionManager.getCollection().size();
        System.out.println("Дата инициализации коллекции: " + time.toString().substring(0, 10));
        // Формируем строку-ответ
        String info = "Тип коллекции: " + type + '\n'
                + "Дата инициализации коллекции: " + time.toString().substring(0, 10) + " "
                + time.toString().substring(11, 19) + '\n'
                + "Размер коллекции: " + size + "\n";

        // Возвращаем ответ клиенту
        return new CommandResponse(info, true);
    }


    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "выводит информацию о коллекции";
    }
}
