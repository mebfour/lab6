package Collection;

import Classes.Route;

import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Класс для управления коллекцией маршрутов.
 * Инкапсулирует логику работы с данными.
 */
public class RouteCollectionManager {
    private final LinkedHashMap<Integer, Route> routeList;
    private final Date initializationDate;
    private final IdGenerator idGenerator;

    /**
     * Штука для расширения, будут использовать наследники
     */
    protected RouteCollectionManager(
            LinkedHashMap<Integer, Route> routeList,
            Date initializationDate,
            IdGenerator idGenerator
    ) {
        this.routeList = routeList;
        this.initializationDate = initializationDate;
        this.idGenerator = idGenerator;
    }

    /**
     * В этой лабе пользуемся обычным хранителем командъ
     */
    public static RouteCollectionManager createDefault(){
        return new RouteCollectionManager(
                new LinkedHashMap<>(),
                new Date(),
                new IdGenerator()

        );

    }



}
