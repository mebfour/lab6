package Commands.Modif;

import Collection.RouteCollectionManager;
import Commands.Command;
import Commands.CommandResponse;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 *  Интерфейс для команд, изменяющих коллекцию
 */
public interface ModificationComman extends Command {

   @Override
    CommandResponse execute(String args);

}
