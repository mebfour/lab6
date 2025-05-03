package Server.Commands;

/**
 * Прародитель всех комманд
 *
 */
public interface Command {
    /**
     * будет удобнее передавать сюда параментры для команд, требующих обработки ввода этих параметров
     * @param args
     */
    void execute(String args);

    String getName();
    String getDescription();
}
