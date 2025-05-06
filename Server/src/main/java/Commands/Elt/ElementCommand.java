package Commands.Elt;

import Commands.Command;
import Classes.Route;

/**
 * Интерфейс для команд, работающих с элементами
 */
public interface ElementCommand extends Command {
    void setElement(Route element);
}
