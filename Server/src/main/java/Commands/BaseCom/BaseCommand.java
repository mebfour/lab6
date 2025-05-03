package Server.Commands.BaseCom;

import Server.Commands.Command;
import CollectionManagement.RouteCollectionManager;

/**
 * Абстрактный класс для команд, работающих с общими полями
 */

public abstract class BaseCommand implements Command {

    protected RouteCollectionManager collection;

    public BaseCommand(RouteCollectionManager collection){
        this.collection = collection;
    }

    @Override
    public void execute(String args) {

    }

    @Override
    public String getDescription() {
        return "";
    }
}
