package Commands.Modif;

import Classes.Route;
import Commands.Command;
import Commands.CommandResponse;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static managers.CommandManager.collectionManager;

public class RemoveLower implements Command {
    @Override

    public CommandResponse execute(String arg) {
        String inpKey = (arg != null) ? arg.trim() : "";

        if (inpKey.isEmpty()) {
            return new CommandResponse("Ключ не передан.", false);
        }

        LinkedHashMap<String, Route> routeList = collectionManager.getCollection();

        if (routeList.isEmpty()) {
            return new CommandResponse("Коллекция пуста! Введите add для добавления нового элемента.", false);
        }

        if (!routeList.containsKey(inpKey)) {
            return new CommandResponse("Элемент с ключом " + inpKey + " не найден.", false);
        }

        Iterator<Map.Entry<String, Route>> it = routeList.entrySet().iterator();
        boolean done = false;
        int removedCount = 0;

        while (it.hasNext()) {
            Map.Entry<String, Route> entry = it.next();
            // Удаляем до и включая ключ
            it.remove();
            removedCount++;
            done = true;
            if (entry.getKey().equals(inpKey)) {
                break;
            }
        }

        if (done) {
            collectionManager.saveToFile();
            return new CommandResponse("Элементы до и включая ключ " + inpKey + " успешно удалены (" + removedCount + " шт.)", true);
        } else {
            return new CommandResponse("Не было элементов для удаления.", false);
        }
    }

    @Override
    public String getName() {
        return "remove_lower";
    }

    @Override
    public String getDescription() {
        return "удаляет элементы меньше заданного";
    }
}
