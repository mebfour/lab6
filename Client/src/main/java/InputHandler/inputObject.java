package InputHandler;

import Classes.Coordinates;
import Classes.Location;
import Classes.Route;

import static ToStart.UserSession.currentUsername;

public class inputObject {

    public static Route inputObject(Route route, InputProvider inputProvider) {
        // Устанавливаем название
        route.setName(inputProvider.readString("Введите название:"));
        if (route.getKey() == null) {
            route.setKey(route.getName());
        }
        // Устанавливаем координаты
        Coordinates coordinates = new Coordinates();
        coordinates.setX(inputProvider.readLong("Введите x:", -326));
        coordinates.setY(inputProvider.readInt("Введите y:", -258));
        route.setCoordinates(coordinates);

        // Устанавливаем локацию "куда"
        Location locationTo = new Location();
        locationTo.setName(inputProvider.readString("Введите, куда вы хотите попасть:"));
        locationTo.setX(inputProvider.readFloat("Введите x:"));
        locationTo.setY(inputProvider.readInt("Введите y:", Integer.MIN_VALUE));
        locationTo.setZ(inputProvider.readInt("Введите z:", Integer.MIN_VALUE));
        route.setTo(locationTo);

        // Устанавливаем локацию "откуда"
        Location locationFrom = new Location();
        locationFrom.setName(inputProvider.readString("Введите, откуда вы добираетесь:"));
        locationFrom.setX(inputProvider.readFloat("Введите x:"));
        locationFrom.setY(inputProvider.readInt("Введите y:", Integer.MIN_VALUE));
        locationFrom.setZ(inputProvider.readInt("Введите z:", Integer.MIN_VALUE));
        route.setFrom(locationFrom);
        route.setOwner(currentUsername);
        return route;
    }


}
