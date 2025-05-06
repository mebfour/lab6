package Commands.Modif;

import Commands.Command;
import Commands.CommandResponse;

import java.util.LinkedHashMap;

import static Collection.RouteCollectionManager.routeList;

public class Clear implements Command {
    @Override
    public CommandResponse execute(String args) {
        routeList.clear();

        return new CommandResponse("Коллекция очищена успешно!", true);
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
