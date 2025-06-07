package Commands.Modif;

import Classes.Route;
import Collection.RouteCollectionManager;
import Commands.Command;
import Commands.CommandResponse;
import InputHandler.RouteRequest;
import com.google.gson.Gson;
import sql.DataSourceProvider;
import javax.sql.DataSource;

import static InputHandler.RouteRequest.isAuthorized;


public class AddCommand implements Command {
    private final RouteCollectionManager collectionManager;
    final Gson gson = new Gson();
    public AddCommand(RouteCollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    @Override
    public CommandResponse execute(String jsonArgs) {
        DataSource ds = DataSourceProvider.getDataSource();
        if (ds == null) {
            System.out.println("Пустое!!!!");
        }

        try {
            RouteRequest request = gson.fromJson(jsonArgs, RouteRequest.class);

            String username = request.getUsername();
            String password = request.getPassword();
            Route route = gson.fromJson(jsonArgs, Route.class);


//            if (username == null || password == null || username.isBlank() || password.isBlank()) {
//                return new CommandResponse("Требуется авторизация: логин и пароль обязательны", false);
//            }

//            if (!isAuthorized(username, password)) {
//                return new CommandResponse("Авторизация не пройдена: неверный логин или пароль", false);
//            }


            int id = (int) collectionManager.generateNextId();
            System.out.println("id: " + id);
            System.out.println(route);
            route.setId(id);

            // Устанавливаем владельца объекта
            //route.setOwner(username); // Добавьте поле owner в Route


            // После добавления - сохранить коллекцию в файл
       //     collectionManager.saveToFile();

            collectionManager.saveToBD(route);

            return new CommandResponse("Маршрут успешно добавлен!", true);
        } catch (Exception e) {

            //удали
            e.printStackTrace();
            return new CommandResponse("Ошибка добавления маршрута", false);
        }
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "добавляет новый объект в конец коллекции";
    }
}
