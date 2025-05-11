package Commands.BaseCom;

import Classes.Route;
import Commands.Command;
import Commands.CommandResponse;

import static Collection.RouteCollectionManager.routeList;

public class ShowCommand implements Command {

    @Override
    public CommandResponse execute(String args) {
        if (!routeList.isEmpty()) {

            StringBuilder sb = new StringBuilder();
            for (Route currentRoute : routeList.values()) {
                sb.append(currentRoute).append("\n\n");
            }
            return new CommandResponse(sb.toString(), true);
        } else {
            return new CommandResponse("Коллекция пуста! Введите add для добавления нового элемента.", false);
        }
    }




    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "выводит коллекцию";
    }

}

