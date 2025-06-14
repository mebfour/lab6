package Commands.Modif;

import Classes.Route;
import Commands.Command;
import Commands.CommandResponse;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static Collection.RouteCollectionManager.routeList;
import static managers.CommandManager.collectionManager;
import static users.LoginCommand.username;

public class RemoveGreater implements Command {
    @Override
    public CommandResponse execute(String arg) {

        String inpKey = (arg != null) ? arg.trim() : "";

        if (inpKey.isEmpty()) {
            return new CommandResponse("Ключ не передан.", false);
        }

        Map<String, Route> routeList = collectionManager.getCollection();

        if (routeList.isEmpty()) {
            return new CommandResponse("Коллекция пуста! Введите add для добавления нового элемента.", false);
        }

        if (!routeList.containsKey(inpKey)) {
            return new CommandResponse("Элемент с ключом " + inpKey + " не найден.", false);
        }
        synchronized(routeList) {
            Iterator<Map.Entry<String, Route>> it = routeList.entrySet().iterator();
            boolean foundKey = false;
            boolean done = false;
            int removedCount = 0;

            while (it.hasNext()) {
                Map.Entry<String, Route> entry = it.next();
                if (foundKey) {
                    if (routeList.get(entry.getKey()).getOwner().equals(username)) {
                        collectionManager.removeConcrFromBD(entry.getValue().getKey());
                        it.remove();
                        removedCount++;
                        done = true;
                    }
                } else if (entry.getKey().equals(inpKey)) {
                    foundKey = true;
                }
            }

            if (done) {
                // collectionManager.saveToFile();
                return new CommandResponse("Элементы, следующие за ключом " + inpKey + ", успешно удалены (" + removedCount + " шт.)", true);
            } else {
                return new CommandResponse("После ключа " + inpKey + " не было элементов для удаления.", false);
            }
        }
    }


    @Override
    public String getName() {
        return "remove_greater";
    }

    @Override
    public String getDescription() {
        return "удаляет элементы, превышающие заданный";
    }
}
