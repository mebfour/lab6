package Commands.Modif;


import Classes.Route;
import Collection.RouteCollectionManager;
import Commands.Command;
import Commands.CommandResponse;


import java.util.LinkedHashMap;

public class ReplaceIfLowe implements Command {
    private final RouteCollectionManager collectionManager;

    public ReplaceIfLowe(RouteCollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public CommandResponse execute(String arg) {
        if (arg == null || arg.trim().isEmpty()) {
            return new CommandResponse("Не переданы ключи.", false);
        }

        // Парсим строку на два ключа
        String[] keys = arg.trim().split("\\s+", 2);
        if (keys.length < 2) {
            return new CommandResponse("Должно быть передано два ключа через пробел.", false);
        }
        String targetKey = keys[0].trim();
        String newKey = keys[1].trim();

        LinkedHashMap<String, Route> routeList = collectionManager.getCollection();

        if (routeList.isEmpty()) {
            return new CommandResponse("Коллекция пуста! Введите add для добавления нового элемента.", false);
        }
        if (!routeList.containsKey(targetKey)) {
            return new CommandResponse("Элемент с ключом " + targetKey + " не найден.", false);
        }

        Route routeToModify = routeList.get(targetKey);

        // Сравниваем строки-ключи лексикографически
        if (newKey.compareTo(routeToModify.getKey()) < 0) {

            routeToModify.setKey(newKey);
            // Обновляем ключ в самой коллекции
            collectionManager.updateToBD(routeToModify);
            routeList.remove(targetKey);
            routeList.put(newKey, routeToModify);
            collectionManager.saveToFile();
            return new CommandResponse("Ключ успешно заменён на " + newKey, true);
        } else {
            return new CommandResponse("Введённый ключ не меньше уже имеющегося, замена невозможна.", false);
        }
    }

    @Override
    public String getName() {
        return "replace_if_lowe";
    }

    @Override
    public String getDescription() {
        return "заменяет значение по ключу, если новое значение меньше старого";
    }
}

