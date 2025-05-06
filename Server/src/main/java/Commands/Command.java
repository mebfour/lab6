package Commands;

import Commands.CommandResponse;

/**
 * Прародитель всех комманд
 *
 */
public interface Command {
    /**
     * будет удобнее передавать сюда параментры для команд, требующих обработки ввода этих параметров
     * @param args
     */
    CommandResponse execute(String args);

    String getName();
    String getDescription();
}
