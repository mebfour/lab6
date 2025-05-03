package Server.Commands.Elt;

import Server.Commands.Command;
import Objects.Route;

/**
 * Интерфейс для команд, работающих с элементами
 */
public interface ElementCommand extends Command {
    void setElement(Route element);
}
