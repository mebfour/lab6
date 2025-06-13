package Commands.Modif;

import Classes.Route;
import Commands.Command;
import Commands.CommandResponse;

import java.util.Iterator;
import java.util.Map;

import static Collection.RouteCollectionManager.routeList;
import static managers.CommandManager.collectionManager;
import static users.LoginCommand.username;

public class Clear implements Command {
    @Override
    public CommandResponse execute(String args) {
        int removedCount = 0;
        synchronized(routeList) {
            Iterator<Map.Entry<String, Route>> it = routeList.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Route> entry = it.next();
                if (routeList.get(entry.getKey()).getOwner().equals(username)) {
                    collectionManager.removeConcrFromBD(entry.getValue().getKey());
                    it.remove();
                    removedCount++;
                }
            }
        }

        if (removedCount == 0) {
            return new CommandResponse("Нет элементов для удаления", false);
        }
        return new CommandResponse("Элементы успешно удалены (" + removedCount + " шт.)", true);
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "очищает коллекцию";
    }

}
