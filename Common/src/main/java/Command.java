import java.io.Serializable;

/**
 * Наследуемся от seriazable, так как
 */


public interface Command extends Serializable {
    /**
     * будет удобнее передавать сюда параментры для команд, требующих обработки ввода этих параметров
     * @param args
     */
    void execute(String args);

    String getName();
    String getDescription();
}