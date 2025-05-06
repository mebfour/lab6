package Commands.BaseCom;

import Commands.CommandResponse;
import Commands.Command;
import Collection.RouteCollectionManager;

/**
 * Абстрактный класс для команд, работающих с общими полями
 */

public abstract class BaseCommand implements Command {

    protected RouteCollectionManager collection;

    public BaseCommand(RouteCollectionManager collection){
        this.collection = collection;
    }

    @Override
    public CommandResponse execute(String args) {
        return null;
    }

    @Override
    public String getDescription() {
        return "";
    }
}
